package com.campus.food.controller;

import com.campus.food.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api")
public class ShopController {

    @Autowired private EntityManager em;

    // ====== 店铺 ======
    @GetMapping("/shops")
    public List<Shop> getShops(
            @RequestParam(required = false) String canteen,
            @RequestParam(required = false) String spicy,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {
        StringBuilder jpql = new StringBuilder("SELECT s FROM Shop s WHERE 1=1");
        if (canteen != null && !canteen.isEmpty()) jpql.append(" AND s.canteen = :canteen");
        if (spicy != null && !spicy.isEmpty()) jpql.append(" AND s.spicy = :spicy");
        if (minPrice != null) jpql.append(" AND s.price >= :minPrice");
        if (maxPrice != null) jpql.append(" AND s.price <= :maxPrice");
        jpql.append(" ORDER BY s.score DESC");

        TypedQuery<Shop> query = em.createQuery(jpql.toString(), Shop.class);
        if (canteen != null && !canteen.isEmpty()) query.setParameter("canteen", canteen);
        if (spicy != null && !spicy.isEmpty()) query.setParameter("spicy", spicy);
        if (minPrice != null) query.setParameter("minPrice", minPrice);
        if (maxPrice != null) query.setParameter("maxPrice", maxPrice);
        return query.getResultList();
    }

    @GetMapping("/shops/hot")
    public List<Shop> getHotShops() {
        return em.createQuery("SELECT s FROM Shop s ORDER BY s.favCount DESC", Shop.class)
                .setMaxResults(10).getResultList();
    }

    @GetMapping("/shops/{id}")
    public Shop getShop(@PathVariable Long id) {
        return em.find(Shop.class, id);
    }

    // ====== 搜索 ======
    @GetMapping("/search")
    public List<Shop> search(@RequestParam String keyword) {
        return em.createQuery(
            "SELECT s FROM Shop s WHERE s.name LIKE :kw OR s.canteen LIKE :kw OR s.dishes LIKE :kw",
            Shop.class)
            .setParameter("kw", "%" + keyword + "%")
            .getResultList();
    }

    // ====== 评论 ======
    @GetMapping("/comments/{shopId}")
    public List<Comment> getComments(@PathVariable Long shopId) {
        return em.createQuery("SELECT c FROM Comment c WHERE c.shopId = :sid ORDER BY c.createTime DESC", Comment.class)
                .setParameter("sid", shopId).getResultList();
    }

    @PostMapping("/comments")
    @Transactional
    public Map<String, Object> postComment(@RequestBody Map<String, Object> body) {
        Comment c = new Comment();
        c.setShopId(Long.valueOf(body.get("shopId").toString()));
        c.setUserId(Long.valueOf(body.getOrDefault("userId", "1").toString()));
        c.setUsername((String) body.getOrDefault("username", "匿名"));
        c.setContent((String) body.get("content"));
        em.persist(c);

        // 更新评论数
        Shop shop = em.find(Shop.class, c.getShopId());
        if (shop != null) {
            shop.setCommentCount(shop.getCommentCount() == null ? 1 : shop.getCommentCount() + 1);
            em.merge(shop);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("comment", c);
        return result;
    }
}
