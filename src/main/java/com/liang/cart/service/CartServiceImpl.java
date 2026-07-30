package com.liang.cart.service;

import com.liang.cart.dto.CartItemRequest;
import com.liang.cart.dto.CartResponse;
import com.liang.cart.entity.Cart;
import com.liang.cart.entity.CartItem;
import com.liang.cart.mapper.CartMapper;
import com.liang.cart.repository.CartItemRepository;
import com.liang.cart.repository.CartRepository;
import com.liang.category.entity.Status;
import com.liang.product.entity.Product;
import com.liang.product.repository.ProductRepository;
import com.liang.shared.api.NotFoundException;
import com.liang.shared.metadata.Metadata;
import com.liang.shared.metadata.MetadataHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final CartMapper cartMapper;

    @Override
    public Page<CartResponse> getAllCarts(Pageable pageable) {
        return cartRepository.findAll(pageable)
                .map(cartMapper::toCartResponse);
    }

    @Override
    @MetadataHandler
    public CartResponse getCartByUserId(Metadata metadata) {
        Cart cart = cartRepository.findByUserIdWithDetails(metadata.getUserId())
                .orElseThrow(() -> new NotFoundException("Cart not found for user ID: " + metadata.getUserId()));
        return cartMapper.toCartResponse(cart);
    }

    @Override
    @MetadataHandler
    public CartResponse viewMine(Metadata metadata) {
        Cart cart = getOrCreateCart(metadata.getUserId());
        return cartMapper.toCartResponse(cart);
    }

    @Override
    @MetadataHandler
    @Transactional
    public CartResponse addItem(Metadata metadata, CartItemRequest request) {
        Cart cart = getOrCreateCart(metadata.getUserId());

        Product product = productRepository.findByIdAndStatus(request.getProductId(), Status.ACTIVE)
                .orElseThrow(() -> new NotFoundException("Product not found or currently unavailable"));

        CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setCart(cart);
                    newItem.setProduct(product);
                    newItem.setQuantity(0);
                    cart.getItems().add(newItem);
                    return newItem;
                });

        int newQuantity = item.getQuantity() + request.getQuantity();
        if (newQuantity > product.getStockQuantity()) {
            throw new IllegalStateException("Not enough stock available. Remaining stock: " + product.getStockQuantity());
        }

        item.setQuantity(newQuantity);
        cartItemRepository.save(item);

        return cartMapper.toCartResponse(getCartWithDetails(metadata.getUserId()));
    }

    @Override
    @MetadataHandler
    @Transactional
    public CartResponse updateItem(Metadata metadata, Long itemId, Integer quantity) {

        Cart cart = getOrCreateCart(metadata.getUserId());

        CartItem item = findOwnedItem(cart, itemId);

        if (quantity <= 0) {
            cart.getItems().remove(item);
            cartItemRepository.delete(item);
            return cartMapper.toCartResponse(getCartWithDetails(metadata.getUserId()));
        }

        Product product = item.getProduct();

        if (product.getStatus() != Status.ACTIVE) {
            throw new IllegalStateException("This product is no longer active");
        }

        if (quantity > product.getStockQuantity()) {
            throw new IllegalStateException("Not enough stock available. Remaining stock: " + product.getStockQuantity());
        }

        item.setQuantity(quantity);
        cartItemRepository.save(item);

        return cartMapper.toCartResponse(getCartWithDetails(metadata.getUserId()));
    }

    @Override
    @MetadataHandler
    @Transactional
    public CartResponse removeItem(Metadata metadata, Long itemId) {
        Cart cart = getOrCreateCart(metadata.getUserId());
        CartItem item = findOwnedItem(cart, itemId);

        cart.getItems().remove(item);
        cartItemRepository.delete(item);

        return cartMapper.toCartResponse(getCartWithDetails(metadata.getUserId()));
    }

    @Override
    @MetadataHandler
    @Transactional
    public void clear(Metadata metadata) {
        Cart cart = getOrCreateCart(metadata.getUserId());
        cartItemRepository.deleteByCartId(cart.getId());
        cart.getItems().clear();
    }

    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserIdWithDetails(userId)
                .orElseGet(() -> cartRepository.findByUserId(userId)
                        .orElseGet(() -> {
                            try {
                                Cart newCart = new Cart();
                                newCart.setUserId(userId);
                                return cartRepository.saveAndFlush(newCart);
                            } catch (DataIntegrityViolationException e) {
                                return cartRepository.findByUserIdWithDetails(userId)
                                        .orElseThrow(() -> new NotFoundException("Cart creation failed for user: " + userId));
                            }
                        }));
    }

    private Cart getCartWithDetails(Long userId) {
        return cartRepository.findByUserIdWithDetails(userId)
                .orElseThrow(() -> new NotFoundException("Cart not found for user: " + userId));
    }

    private CartItem findOwnedItem(Cart cart, Long itemId) {
        return cart.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Cart item not found or does not belong to your cart"));
    }
}