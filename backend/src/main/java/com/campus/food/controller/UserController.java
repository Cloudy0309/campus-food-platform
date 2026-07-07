package com.campus.food.controller;

import com.campus.food.entity.*;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired private EntityManager em;

    // ====== 用户 ======
    @PostMapping("/user/login")
    public Map<String, Object> login(@RequestBody Map<String, String> body) {
        List<User> users = em.createQuery("SELECT u FROM User u WHERE u.username = :un AND u.password = :pw", User.class)
                .setParameter("un", body.get("username"))
                .setParameter("pw", body.get("password"))
                .getResultList();
        Map<String, Object> result = new HashMap<>();
        if (!users.isEmpty()) {
            User u = users.get(0);
            result.put("success", true);
            result.put("user", Map.of("id", u.getId(), "nickname", u.getNickname(), "username", u.getUsername()));
        } else {
            result.put("success", false);
            result.put("message", "用户名或密码错误");
        }
        return result;
    }

    @PostMapping("/user/register")
    @Transactional
    public Map<String, Object> register(@RequestBody Map<String, String> body) {
        Map<String, Object> result = new HashMap<>();
        List<User> exist = em.createQuery("SELECT u FROM User u WHERE u.username = :un", User.class)
                .setParameter("un", body.get("username")).getResultList();
        if (!exist.isEmpty()) {
            result.put("success", false);
            result.put("message", "用户名已存在");
            return result;
        }
        User u = new User();
        u.setUsername(body.get("username"));
        u.setPassword(body.get("password"));
        u.setNickname(body.getOrDefault("nickname", body.get("username")));
        em.persist(u);
        result.put("success", true);
        result.put("user", Map.of("id", u.getId(), "nickname", u.getNickname(), "username", u.getUsername()));
        return result;
    }

    // ====== 探店笔记 ======
    @GetMapping("/notes")
    public List<Note> getNotes(@RequestParam(required = false) Long userId) {
        if (userId != null) {
            return em.createQuery("SELECT n FROM Note n WHERE n.userId = :uid ORDER BY n.createTime DESC", Note.class)
                    .setParameter("uid", userId).getResultList();
        }
        return em.createQuery("SELECT n FROM Note n ORDER BY n.createTime DESC", Note.class)
                .setMaxResults(10).getResultList();
    }

    @PostMapping("/notes")
    @Transactional
    public Map<String, Object> publishNote(@RequestBody Map<String, Object> body) {
        Note n = new Note();
        n.setUserId(Long.valueOf(body.getOrDefault("userId", "1").toString()));
        n.setShopId(Long.valueOf(body.get("shopId").toString()));
        n.setTitle((String) body.getOrDefault("title", ""));
        n.setContent((String) body.getOrDefault("content", ""));
        n.setStars(Integer.valueOf(body.getOrDefault("stars", "4").toString()));
        n.setTags((String) body.getOrDefault("tags", ""));
        em.persist(n);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("note", n);
        return result;
    }

    // ====== 收藏 ======
    @PostMapping("/favorites/toggle")
    @Transactional
    public Map<String, Object> toggleFav(@RequestBody Map<String, Object> body) {
        Long userId = Long.valueOf(body.getOrDefault("userId", "1").toString());
        Long shopId = Long.valueOf(body.get("shopId").toString());

        List<Favorite> exist = em.createQuery(
            "SELECT f FROM Favorite f WHERE f.userId = :uid AND f.shopId = :sid", Favorite.class)
            .setParameter("uid", userId).setParameter("sid", shopId).getResultList();

        Map<String, Object> result = new HashMap<>();
        if (!exist.isEmpty()) {
            em.remove(exist.get(0));
            result.put("faved", false);
        } else {
            Favorite f = new Favorite();
            f.setUserId(userId);
            f.setShopId(shopId);
            em.persist(f);
            result.put("faved", true);
        }

        // 更新收藏数
        Long count = em.createQuery("SELECT COUNT(f) FROM Favorite f WHERE f.shopId = :sid", Long.class)
                .setParameter("sid", shopId).getSingleResult();
        Shop shop = em.find(Shop.class, shopId);
        if (shop != null) { shop.setFavCount(count.intValue()); em.merge(shop); }

        result.put("success", true);
        return result;
    }

    @GetMapping("/favorites")
    public List<Map<String, Object>> getFavorites(@RequestParam(defaultValue = "1") Long userId) {
        List<Favorite> favs = em.createQuery(
            "SELECT f FROM Favorite f WHERE f.userId = :uid", Favorite.class)
            .setParameter("uid", userId).getResultList();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Favorite f : favs) {
            Shop shop = em.find(Shop.class, f.getShopId());
            if (shop != null) {
                Map<String, Object> item = new HashMap<>();
                item.put("favoriteId", f.getId());
                item.put("shop", shop);
                result.add(item);
            }
        }
        return result;
    }
}
