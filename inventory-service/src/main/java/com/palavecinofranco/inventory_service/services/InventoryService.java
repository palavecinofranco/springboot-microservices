package com.palavecinofranco.inventory_service.services;

import com.palavecinofranco.inventory_service.model.dtos.BaseResponse;
import com.palavecinofranco.inventory_service.model.dtos.OrderItemRequest;
import com.palavecinofranco.inventory_service.model.entites.Inventory;
import com.palavecinofranco.inventory_service.repositories.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    public Boolean isInStock(String sku) {
        var inventory = inventoryRepository.findBySku(sku);
        return inventory.filter(value->value.getQuantity()>0).isPresent();

    }

    public BaseResponse areInStock(List<OrderItemRequest> orderItems) {
        ArrayList<String> errorList = new ArrayList<String>();

        List<String> skus = orderItems.stream().map(OrderItemRequest::getSku).toList();

        List<Inventory> inventories = inventoryRepository.findBySkuIn(skus);

        orderItems.forEach(orderItemRequest -> {
            var inventory = inventories.stream().filter(value->value.getSku().equals(orderItemRequest.getSku())).findFirst();
            if(inventory.isEmpty()){
                errorList.add("Product with SKU " + orderItemRequest.getSku() + " does not exist");
            } else if (inventory.get().getQuantity()<orderItemRequest.getQuantity()) {
                errorList.add("Product with SKU " + orderItemRequest.getSku() + " has insufficient quantity");
            }
        });

        return errorList.size()>0 ? new BaseResponse(errorList.toArray(new String[0])) : new BaseResponse(null);
    }
}
