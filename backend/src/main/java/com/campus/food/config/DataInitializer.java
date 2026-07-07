package com.campus.food.config;

import com.campus.food.entity.*;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private EntityManager em;

    @Override
    @Transactional
    public void run(String... args) {
        if (em.createQuery("SELECT COUNT(s) FROM Shop s", Long.class).getSingleResult() > 0) return;

        Shop s1 = new Shop(); s1.setName("麻辣香锅"); s1.setCanteen("一食堂"); s1.setAddress("一食堂二楼东侧");
        s1.setPrice(18.0); s1.setScore(4.8); s1.setHours("10:00-21:00");
        s1.setDishes("麻辣香锅套餐,酸辣土豆丝,番茄牛腩锅"); s1.setDescription("一食堂人气最高窗口，荤素自选，秘制酱料");
        s1.setCategory("食堂"); s1.setSpicy("辣"); s1.setFavCount(128); s1.setCommentCount(45);
        em.persist(s1);

        Shop s2 = new Shop(); s2.setName("铁板牛肉饭"); s2.setCanteen("二食堂"); s2.setAddress("二食堂一楼");
        s2.setPrice(16.0); s2.setScore(4.7); s2.setHours("10:30-20:30");
        s2.setDishes("铁板牛肉饭,黑椒鸡排饭"); s2.setDescription("回头率最高，牛肉嫩滑多汁");
        s2.setCategory("食堂"); s2.setSpicy("不辣"); s2.setFavCount(96); s2.setCommentCount(32);
        em.persist(s2);

        Shop s3 = new Shop(); s3.setName("酸菜鱼米线"); s3.setCanteen("校外小吃"); s3.setAddress("校外小吃街中段");
        s3.setPrice(15.0); s3.setScore(4.9); s3.setHours("11:00-22:00");
        s3.setDishes("酸菜鱼米线,番茄鱼米线,麻辣鱼米线"); s3.setDescription("排队王，酸菜鱼片鲜嫩，汤底浓郁");
        s3.setCategory("校外小吃"); s3.setSpicy("微辣"); s3.setFavCount(156); s3.setCommentCount(58);
        em.persist(s3);

        Shop s4 = new Shop(); s4.setName("杨枝甘露"); s4.setCanteen("奶茶"); s4.setAddress("奶茶专区");
        s4.setPrice(12.0); s4.setScore(4.6); s4.setHours("09:00-22:00");
        s4.setDishes("杨枝甘露,芒果西米露,椰奶冻"); s4.setDescription("夏日必喝，芒果新鲜，西柚酸甜");
        s4.setCategory("奶茶"); s4.setSpicy("不辣"); s4.setFavCount(89); s4.setCommentCount(28);
        em.persist(s4);

        Shop s5 = new Shop(); s5.setName("鸡胸肉沙拉"); s5.setCanteen("减脂餐"); s5.setAddress("减脂餐专窗");
        s5.setPrice(20.0); s5.setScore(4.5); s5.setHours("10:00-19:00");
        s5.setDishes("鸡胸肉沙拉,鲜虾沙拉,牛油果沙拉"); s5.setDescription("健身党最爱，食材新鲜，酱汁低卡");
        s5.setCategory("减脂餐"); s5.setSpicy("不辣"); s5.setFavCount(67); s5.setCommentCount(19);
        em.persist(s5);

        Shop s6 = new Shop(); s6.setName("日式拉面"); s6.setCanteen("二食堂"); s6.setAddress("二食堂二楼");
        s6.setPrice(15.0); s6.setScore(4.8); s6.setHours("10:30-21:00");
        s6.setDishes("豚骨拉面,味噌拉面,地狱拉面"); s6.setDescription("汤底浓郁，叉烧厚切，溏心蛋完美");
        s6.setCategory("食堂"); s6.setSpicy("微辣"); s6.setFavCount(112); s6.setCommentCount(36);
        em.persist(s6);

        Shop s7 = new Shop(); s7.setName("炸串卷饼"); s7.setCanteen("校外小吃"); s7.setAddress("校外小吃街路口");
        s7.setPrice(10.0); s7.setScore(4.7); s7.setHours("16:00-23:00");
        s7.setDishes("炸串卷饼,烤面筋,炸鸡柳"); s7.setDescription("隐藏宝藏，秘制酱料，夜宵首选");
        s7.setCategory("校外小吃"); s7.setSpicy("中辣"); s7.setFavCount(78); s7.setCommentCount(25);
        em.persist(s7);

        Shop s8 = new Shop(); s8.setName("芝士葡萄"); s8.setCanteen("奶茶"); s8.setAddress("奶茶专区");
        s8.setPrice(14.0); s8.setScore(4.4); s8.setHours("09:00-22:00");
        s8.setDishes("芝士葡萄,多肉葡萄,葡萄果茶"); s8.setDescription("清爽不腻，爆珠Q弹，回购无数");
        s8.setCategory("奶茶"); s8.setSpicy("不辣"); s8.setFavCount(55); s8.setCommentCount(15);
        em.persist(s8);

        // 示例评论
        Comment c1 = new Comment(); c1.setShopId(1L); c1.setUserId(1L); c1.setUsername("吃货小王");
        c1.setContent("这家麻辣香锅真的绝了！分量足，味道正宗，每次来都要排队但值得等！");
        em.persist(c1);
        Comment c2 = new Comment(); c2.setShopId(1L); c2.setUserId(1L); c2.setUsername("美食猎人");
        c2.setContent("人均不到20就能吃撑，性价比超高。强烈推荐牛腩锅！");
        em.persist(c2);
    }
}
