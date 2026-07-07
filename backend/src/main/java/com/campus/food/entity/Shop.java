package com.campus.food.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name = "shops")
public class Shop {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String canteen;
    private String address;
    private Double price;
    private Double score;
    private String hours;
    private String dishes;
    private String description;
    private String image;
    private String category;
    private String spicy;
    private Integer favCount;
    private Integer commentCount;
    private LocalDateTime createTime = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCanteen() { return canteen; }
    public void setCanteen(String canteen) { this.canteen = canteen; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
    public String getHours() { return hours; }
    public void setHours(String hours) { this.hours = hours; }
    public String getDishes() { return dishes; }
    public void setDishes(String dishes) { this.dishes = dishes; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getSpicy() { return spicy; }
    public void setSpicy(String spicy) { this.spicy = spicy; }
    public Integer getFavCount() { return favCount; }
    public void setFavCount(Integer favCount) { this.favCount = favCount; }
    public Integer getCommentCount() { return commentCount; }
    public void setCommentCount(Integer commentCount) { this.commentCount = commentCount; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
