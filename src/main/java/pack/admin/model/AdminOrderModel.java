package pack.admin.model;

import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import jakarta.transaction.Transactional;
import pack.dto.OrderDto;
import pack.entity.Order;
import pack.entity.Product;
import pack.repository.OrdersRepository;
import pack.repository.ProductsRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class AdminOrderModel {

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private ProductsRepository productsRepository;

    // 최신순 정렬된 전체 주문 목록
    public Page<OrderDto> listAll(Pageable pageable) {
        Pageable sortedByDateDesc = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "date"));
        Page<Order> orders = ordersRepository.findAllByOrderDESC(sortedByDateDesc);
        return orders.map(Order::toDto);
    }

    // 검색 조건에 따라 주문 목록 불러오기 (최신순 정렬)
    public Page<OrderDto> searchOrders(Pageable pageable, String searchTerm, String searchField, String startDate, String endDate) {
        Pageable sortedByDateDesc = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "date"));
        Page<Order> orders;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        switch (searchField) {
            case "userId":
                orders = ordersRepository.findByUserIdContainingIgnoreCase(searchTerm, sortedByDateDesc);
                break;
            case "state":
                orders = ordersRepository.findByStateContainingIgnoreCase(searchTerm, sortedByDateDesc);
                break;
            case "date":
                LocalDateTime start = LocalDateTime.parse(startDate + " 00:00:00", formatter);
                LocalDateTime end = LocalDateTime.parse(endDate + " 23:59:59", formatter);
                orders = ordersRepository.findByDateBetween(start, end, sortedByDateDesc);
                break;
            default:
                orders = ordersRepository.findAll(sortedByDateDesc);
                break;
        }

        return orders.map(Order::toDto);
    }

    public OrderDto getData(Integer no) {
        return Order.toDto(ordersRepository.findByNo(no));
    }

    public Map<Integer, String> getProductInfo(List<Integer> productNoList) {
        Map<Integer, String> productInfo = new HashMap<>();

        for (Integer i : productNoList) {
            String productName = productsRepository.findById(i)
                    .map(Product::getName)
                    .orElse("Unknown Product");
            productInfo.put(i, productName);
        }

        return productInfo;
    }

    @Transactional
    public String updateOrderStatus(Integer orderNo, String status) {
        try {
            Order order = ordersRepository.findById(orderNo)
                    .orElseThrow(() -> new IllegalStateException("ID가 " + orderNo + "인 주문을 찾을 수 없습니다."));

            order.setState(status);  // 상태 업데이트
            ordersRepository.save(order);  // 변경사항 저장
            return "주문 상태가 성공적으로 업데이트되었습니다.";
        } catch (Exception e) {
            System.err.println("주문 상태 업데이트 중 오류 발생: " + e.getMessage());
            throw e;
        }
    }
}
