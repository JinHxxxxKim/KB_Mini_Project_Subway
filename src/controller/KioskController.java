package controller;

import dto.IngredientDTO;
import dto.MemberDTO;
import dto.MemberOrderDTO;
import dto.MenuDTO;
import exception.member.MemberException;
import exception.order.OrderException;
import service.AdminService;
import service.MemberService;
import service.OrderService;
import service.impl.AdminServiceImpl;
import service.impl.MemberServiceImpl;
import service.impl.OrderServiceImpl;
import view.FailView;
import view.KioskView;
import view.SuccesssView;
import vo.HistoryVo;
import vo.OrderVo;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import static exception.constant.MemberExceptionType.NOT_FOUND_MEMBER_ERROR;
import static exception.constant.OrderExceptionType.*;

public class KioskController {

    public static int additionalMenu;
    private static Scanner sc = new Scanner(System.in);
    private static MemberService memberService = MemberServiceImpl.getInstance();
    private static OrderService orderService = OrderServiceImpl.getInstance();
    private static AdminService adminService = AdminServiceImpl.getInstance();

    private static ArrayList<OrderVo> cart = new ArrayList<>();

    /**
     * 멤버 유효성 검사
     * 유효성 검사 통과 시, menu choice 화면으로 이동
     * @param memberPhoneNumber 사용자로부터 입력받은 사용자 전화번호
     */
    public static void checkMember(String memberPhoneNumber) {
        try {
            MemberDTO dto = memberService.findByPhoneNumber(memberPhoneNumber);
            // MenuView로 바로 갈지 or SuccessView 통해서 갈지 고려
            // 현재는 바로 MenuView로 호출
            KioskView.startOrder(dto);
        } catch (MemberException e) {
            FailView.errorMessage(NOT_FOUND_MEMBER_ERROR.getErrorCode(), NOT_FOUND_MEMBER_ERROR.getErrorMessage());
        } catch (OrderException e) {
            FailView.errorMessage(NOT_FOUND_MENU_ERROR.getErrorCode(), NOT_FOUND_MENU_ERROR.getErrorMessage());
        }
    }

    /**
     * method order : 주문 메소드
     * @param memberId 주문하는 사용자의 ID
     * @param vo 사용자의 입력 VO
     */
    public static void order(Long memberId, OrderVo vo) {
        try {
            cart.add(vo);
            KioskView.addCartOrPay(memberId, cart);
        } catch (OrderException e) {
            FailView.errorMessage(NOT_FOUND_MENU_ERROR.getErrorCode(), NOT_FOUND_MENU_ERROR.getErrorMessage());
        }
    }

    /**
     * method orderSelectByAll: 모든 주문 현황 조회
     */
    public static void orderSelectByAll() {
        try {
            List<MemberOrderDTO> allOrderInfo = orderService.findAllOrderInfo();
            SuccesssView.printOrderStatus(allOrderInfo);
        } catch (OrderException e) {
            FailView.errorMessage(NOT_FOUND_ORDER_LIST.getErrorCode(), NOT_FOUND_ORDER_LIST.getErrorMessage());
        }
    }

    /**
     * method menuSelectByAll: 모든 메뉴 조회
     */
    public static int menuSelectByAll() {
        try {
            List<MenuDTO> allMenu = orderService.findAllMenu();
            SuccesssView.printAllMenu(allMenu);
            return allMenu.size();
        } catch (OrderException e) {
            FailView.errorMessage(NOT_FOUND_MENU_LIST.getErrorCode(), NOT_FOUND_MENU_LIST.getErrorMessage());
        }
        return -1;
    }

    /**
     * method breadSelectByAll: 모든 빵 조회
     */
    public static int breadSelectByAll() {
        try {
            List<IngredientDTO> ingredientDTOS = orderService.findIngredientByIngredientCategory(1);
            SuccesssView.printSelect(ingredientDTOS);
            return ingredientDTOS.size();
        } catch (OrderException e) {
            FailView.errorMessage(NOT_FOUND_INGREDIENT_LIST.getErrorCode(), NOT_FOUND_INGREDIENT_LIST.getErrorMessage());
        }
        return -1;
    }

    /**
     * method cheeseSelectByAll: 모든 치즈 조회
     */
    public static int cheeseSelectByAll() {
        try {
            List<IngredientDTO> ingredientDTOS = orderService.findIngredientByIngredientCategory(2);
            SuccesssView.printSelect(ingredientDTOS);
            return ingredientDTOS.size();
        } catch (OrderException e) {
            FailView.errorMessage(NOT_FOUND_INGREDIENT_LIST.getErrorCode(), NOT_FOUND_INGREDIENT_LIST.getErrorMessage());
        }
        return -1;
    }

    /**
     * method additionalMenuSelectByAll: 모든 추가메뉴 확인
     */
    public static int additionalMenuSelectByAll() {
        try {
            List<IngredientDTO> ingredientDTOS = orderService.findIngredientByIngredientCategory(3);
            SuccesssView.printSelect(ingredientDTOS);
            return ingredientDTOS.size();
        } catch (OrderException e) {
            FailView.errorMessage(NOT_FOUND_INGREDIENT_LIST.getErrorCode(), NOT_FOUND_INGREDIENT_LIST.getErrorMessage());
        }
        return -1;
    }

    /**
     * method vegetableSelectByAll: 모든 채소 조회
     */
    public static int vegetableSelectByAll() {
        try {
            List<IngredientDTO> ingredientDTOS = orderService.findIngredientByIngredientCategory(4);
            SuccesssView.printSelect(ingredientDTOS);
            return ingredientDTOS.size();
        } catch (OrderException e) {
            FailView.errorMessage(NOT_FOUND_INGREDIENT_LIST.getErrorCode(), NOT_FOUND_INGREDIENT_LIST.getErrorMessage());
        }
        return -1;
    }

    /**
     * method sourceSelectByAll: 모든 소스 조회
     */
    public static int sourceSelectByAll() {
        try {
            List<IngredientDTO> ingredientDTOS = orderService.findIngredientByIngredientCategory(5);
            SuccesssView.printSelect(ingredientDTOS);
            return ingredientDTOS.size();
        } catch (OrderException e) {
            FailView.errorMessage(NOT_FOUND_INGREDIENT_LIST.getErrorCode(), NOT_FOUND_INGREDIENT_LIST.getErrorMessage());
        }
        return -1;
    }

    /**
     * method getMemberOrderHistory: 멤버의 해당 메뉴에 대한 과거 기록 조회
     * @param memberId 현재 주문 중인 고객의 ID
     * @param menuId 현재 주문 중인 고객이 선택한 메뉴 ID
     */
    public static void getMemberOrderHistory(Long memberId, long menuId) {
        try {
            MemberOrderDTO history = orderService.findHistoryByMemberMenuId(memberId, menuId);
            String selectBread = adminService.findByIngredientId((long) history.getSelectBread()).getIngredientName();
            String selectCheese = adminService.findByIngredientId((long) history.getSelectCheese()).getIngredientName();

            StringTokenizer st = new StringTokenizer(history.getSelectedAdditionalMenu());
            StringBuilder additionalSB = new StringBuilder();
            while (st.hasMoreTokens()) {
                additionalSB.append(adminService.findByIngredientId(Long.parseLong(st.nextToken())).getIngredientName());
                additionalSB.append(",");
            }
            additionalSB.deleteCharAt(additionalSB.length()-1);

            st = new StringTokenizer(history.getExcludedVegetable());
            StringBuilder exvegeSB = new StringBuilder();
            while (st.hasMoreTokens()) {
                exvegeSB.append(adminService.findByIngredientId(Long.parseLong(st.nextToken())).getIngredientName());
                exvegeSB.append(",");
            }
            exvegeSB.deleteCharAt(exvegeSB.length()-1);

            st = new StringTokenizer(history.getSelectedSource());
            StringBuilder sourceSB = new StringBuilder();
            while (st.hasMoreTokens()) {
                sourceSB.append(adminService.findByIngredientId(Long.parseLong(st.nextToken())).getIngredientName());
                sourceSB.append(",");
            }
            sourceSB.deleteCharAt(sourceSB.length()-1);

            HistoryVo historyVo = new HistoryVo(
                    selectBread, selectCheese, additionalSB.toString(), exvegeSB.toString(), sourceSB.toString()
            );
            SuccesssView.printMemberOrderDTO(historyVo);
        } catch (OrderException e) {
            FailView.errorMessage(404,"주문 기록이 없습니다.");
        }
    }


    /**
     *
     * @param memberId 결제를 진행할 고객의 ID
     */
    public static void cartPayment(long memberId) {

        MemberOrderDTO orderDTO;
        try {
            for (OrderVo vo : cart) {
                orderDTO = orderService.saveMemberOrder(new MemberOrderDTO(
                        (long) 0,
                        mapping(1, vo.getSelectBread()),
                        mapping(2, vo.getSelectCheese()),
                        mappingString(3, vo.getSelectedAdditionalMenu()),
                        mappingString(4, vo.getExcludedVegetable()),
                        mappingString(5, vo.getSelectedSource()),
                        null,
                        'N',
                        memberId,
                        (long) vo.getMenuId()));
            }
            
            cart.clear();
            SuccesssView.printMessageOrderSuccess("주문성공");
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    /**
     * method findMenuByMenuId: 메뉴 ID를 통해 menuDTO를 얻는 메소드
     * @param cart 현재 주문 중인 고객의 장바구니 자료구조
     */
    public static void findMenuByMenuId(ArrayList<OrderVo> cart) {
        ArrayList<MenuDTO> cartMenu = new ArrayList<>();
        try {
            for (OrderVo vo : cart) {
                MenuDTO menu = orderService.findMenuByMenuId((long) vo.getMenuId());
                List<Long> additionalMenusId =
                        List.of(vo.getSelectedAdditionalMenu().split(" "))
                                .stream().map(additionalMenu -> Long.parseLong(additionalMenu)).collect(Collectors.toList());
                List<IngredientDTO> ingredients = orderService.findIngredientByIngredientCategory(3);
                List<Long> ingredientsId = ingredients.stream().map(ingredient -> ingredient.getIngredientId() - 9L).collect(Collectors.toList());

                additionalMenusId.forEach(additionalMenuId -> {
                    int index = ingredientsId.indexOf(additionalMenuId);
                    if (index >= 0) {
                        IngredientDTO ingredient = ingredients.get(index);
                        menu.setMenuPrice(menu.getMenuPrice() + ingredient.getIngredientPrice());
                    }
                });

                cartMenu.add(menu);
            }
            SuccesssView.printMenuInfo(cartMenu);
        } catch (RuntimeException e) {
            throw new OrderException();
        }
    }

    /**
     * method mapping: 사용자의 입력값과, 실제 DB의 저장된 ingredientPK 값과의 매칭 메소드
     * @param category PK값을 찾고자하는 Ingerdient의 Category
     * @param inputNum 사용자의 입력 값(Integer Type)
     * @return
     */
    private static int mapping(int category, int inputNum) {
        switch (category) {
            case 1:
                return inputNum;
            case 2:
                return inputNum + 6;
        }
        return -1;
    }

    /**
     * method mapping: 사용자의 입력값과, 실제 DB의 저장된 ingredientPK 값과의 매칭 메소드
     * @param category PK값을 찾고자하는 Ingerdient의 Category
     * @param inputSelect 사용자의 입력 값(String Type)
     * @return
     */
    private static String mappingString(int category, String inputSelect) {
        StringTokenizer st = new StringTokenizer(inputSelect);
        StringBuilder sb = new StringBuilder();
        switch (category) {
            case 3: // +9
                while (st.hasMoreTokens()) {
                    sb.append((Integer.parseInt(st.nextToken()) + 9));
                    sb.append(" ");
                }
            case 4: // +15
                while (st.hasMoreTokens()) {
                    sb.append((Integer.parseInt(st.nextToken()) + 15));
                    sb.append(" ");
                }
            case 5: // + 23
                while (st.hasMoreTokens()) {
                    sb.append((Integer.parseInt(st.nextToken()) + 23));
                    sb.append(" ");
                }
        }
        return sb.toString();
    }
}