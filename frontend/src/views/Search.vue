<template>
  <div class="search-page">
    <h2 class="page-title">搜索美食</h2>
    <div class="search-box">
      <el-input v-model="keyword" placeholder="搜索店铺名、菜品、关键词…" size="large" clearable @keyup.enter="doSearch">
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
    </div>

    <div v-if="keyword && results.length" class="search-results">
      <p class="result-count">找到 {{ results.length }} 个结果</p>
      <div class="card-grid">
        <div v-for="shop in results" :key="shop.id" class="food-card" @click="$router.push(`/detail/${shop.id}`)">
          <div class="food-card-img" :class="`c${shop.id % 6 + 1}`">🍽</div>
          <div class="food-card-body">
            <div class="food-card-name">{{ shop.name }}</div>
            <div class="food-card-shop">{{ shop.canteen }} · {{ shop.address }}</div>
            <div class="food-card-meta">
              <span class="food-card-stars">⭐ {{ shop.score }}</span>
              <span class="food-card-price">¥{{ shop.price }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-else-if="keyword && !results.length" class="empty-state">
      <p>😕 没有找到相关结果</p>
      <span>试试其他关键词</span>
    </div>

    <div v-else class="search-hint">
      <p>🔍 搜索店铺、菜品或美食关键词</p>
      <div class="hot-search">
        <span>热门搜索：</span>
        <span v-for="h in hotWords" :key="h" class="hot-word" @click="keyword = h; doSearch()">{{ h }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { search } from '../api'

const keyword = ref('')
const results = ref([])
const hotWords = ['麻辣香锅', '奶茶', '铁板牛肉', '酸菜鱼', '沙拉', '拉面', '炸串']

watch(keyword, (newVal) => {
  if (newVal.trim()) {
    doSearch()
  } else {
    results.value = []
  }
})

async function doSearch() {
  if (!keyword.value.trim()) return
  try {
    const res = await search(keyword.value.trim())
    results.value = res.data
  } catch (e) {
    console.error('Search failed:', e)
    results.value = [
      { id: 1, name: '麻辣香锅', canteen: '一食堂', address: '二楼东侧', score: '4.8', price: 18 },
      { id: 2, name: '铁板牛肉饭', canteen: '二食堂', address: '一楼', score: '4.7', price: 16 },
      { id: 3, name: '酸菜鱼米线', canteen: '校外小吃', address: '小吃街中段', score: '4.9', price: 15 },
      { id: 4, name: '杨枝甘露', canteen: '奶茶', address: '奶茶专区', score: '4.6', price: 12 },
      { id: 5, name: '鸡胸肉沙拉', canteen: '减脂餐', address: '专窗', score: '4.5', price: 20 },
      { id: 6, name: '日式拉面', canteen: '二食堂', address: '二楼', score: '4.8', price: 15 },
      { id: 7, name: '炸串卷饼', canteen: '校外小吃', address: '路口', score: '4.7', price: 10 },
      { id: 8, name: '芝士葡萄', canteen: '奶茶', address: '奶茶专区', score: '4.4', price: 14 },
    ].filter(f => 
      f.name.includes(keyword.value) || f.canteen.includes(keyword.value) || f.address.includes(keyword.value)
    )
  }
}
</script>

<style scoped>
.search-box { max-width: 600px; margin: 0 auto 24px; }
.result-count { font-size: 13px; color: #999; margin-bottom: 14px; }
.empty-state { text-align: center; padding: 60px 0; color: #999; }
.empty-state p { font-size: 18px; margin-bottom: 8px; }
.search-hint { text-align: center; padding: 60px 0; color: #BBB; font-size: 16px; }
.hot-search { margin-top: 20px; font-size: 14px; }
.hot-search span { color: #999; }
.hot-word { color: #FF6600 !important; margin-left: 10px; cursor: pointer; }
.hot-word:hover { text-decoration: underline; }
</style>