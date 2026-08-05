package com.liang.order.service;

import com.liang.order.dto.OrderRequest;
import com.liang.order.dto.OrderRequestUpdate;
import com.liang.order.dto.OrderResponse;
import com.liang.order.dto.OrderResponseDetail;
import com.liang.shared.metadata.Metadata;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    Page<OrderResponseDetail> adminList(Long filterUserId , String orderStatus, Pageable pageable);

    OrderResponseDetail adminView(Long id);

    OrderResponseDetail adminUpdateStatus(Long id , OrderRequestUpdate requestUpdate);

    OrderResponse checkout(Metadata metadata , OrderRequest request);

    Page<OrderResponse> list(Metadata metadata , Pageable pageable);

    OrderResponse view(Metadata metadata , Long id);

    OrderResponse cancel(Metadata metadata , Long id);

}
