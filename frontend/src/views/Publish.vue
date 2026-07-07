<template>
  <div class="publish-page">
    <h2 class="page-title">发布探店笔记</h2>
    <div class="publish-form">
      <el-form label-width="90px">
        <el-form-item label="选择店铺">
          <el-select v-model="form.shopId" placeholder="请选择店铺" style="width:100%" @change="onShopChange">
            <el-option v-for="s in shops" :key="s.id" :label="`${s.name} - ${s.canteen} · ${s.address}`" :value="s.id" />
          </el-select>
        </el-form-item>

        <el-form-item label="探店标题">
          <el-input v-model="form.title" placeholder="给你的探店笔记起个标题" />
        </el-form-item>

        <el-form-item label="上传图片">
          <div class="upload-zone" @click="uploadTrigger">
            <span v-if="!form.images.length">📷 点击上传图片（支持多张，最多9张）</span>
            <div v-else class="upload-previews">
              <span v-for="(img, i) in form.images" :key="i" class="preview-thumb">🖼</span>
              <span v-if="form.images.length < 9" class="preview-add">+</span>
            </div>
          </div>
          <input ref="fileInput" type="file" multiple accept="image/*" style="display:none" @change="handleUpload" />
        </el-form-item>

        <el-form-item label="星级打分">
          <div class="star-rating">
            <span v-for="i in 5" :key="i" :class="{ active: i <= form.stars }" @click="form.stars = i">★</span>
            <span class="star-text">{{ form.stars }}.0 分</span>
          </div>
        </el-form-item>

        <el-form-item label="探店文案">
          <el-input v-model="form.content" type="textarea" :rows="5" placeholder="分享你的探店体验，推荐必点菜品，描述口味、环境、服务…" />
        </el-form-item>

        <el-form-item label="标签选择">
          <div class="tag-options">
            <span v-for="t in tags" :key="t" :class="{ selected: form.tags.includes(t) }" @click="toggleTag(t)">{{ t }}</span>
          </div>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" size="large" style="width:100%;background:#FF6600;border-color:#FF6600;border-radius:22px" @click="submit" :loading="submitting">✏ 提交发布</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getShops, publishNote } from '../api'

const router = useRouter()
const fileInput = ref(null)
const submitting = ref(false)
const shops = ref([])

const tags = ['热门', '新店', '性价比高', '排队王', '小众推荐', '环境好', '服务优', '分量足']

const form = reactive({
  shopId: '',
  title: '',
  images: [],
  stars: 4,
  content: '',
  tags: []
})

onMounted(() => {
  loadShops()
})

async function loadShops() {
  try {
    const res = await getShops()
    shops.value = res.data
  } catch (e) {
    console.error('Load shops failed:', e)
    shops.value = [
      { id: 1, name: '麻辣香锅', canteen: '一食堂', address: '二楼东侧' },
      { id: 2, name: '铁板牛肉饭', canteen: '二食堂', address: '一楼' },
      { id: 3, name: '酸菜鱼米线', canteen: '校外小吃', address: '小吃街中段' },
      { id: 4, name: '杨枝甘露', canteen: '奶茶', address: '奶茶专区' },
      { id: 5, name: '鸡胸肉沙拉', canteen: '减脂餐', address: '专窗' },
      { id: 6, name: '日式拉面', canteen: '二食堂', address: '二楼' },
      { id: 7, name: '炸串卷饼', canteen: '校外小吃', address: '路口' },
      { id: 8, name: '芝士葡萄', canteen: '奶茶', address: '奶茶专区' },
    ]
  }
}

function onShopChange() {
  if (!form.title) {
    const shop = shops.value.find(s => s.id === form.shopId)
    if (shop) form.title = `探店：${shop.name}`
  }
}

function uploadTrigger() { fileInput.value.click() }
function handleUpload(e) {
  const files = Array.from(e.target.files)
  files.forEach(f => { if (form.images.length < 9) form.images.push(f.name) })
  e.target.value = ''
}
function toggleTag(t) {
  const idx = form.tags.indexOf(t)
  if (idx > -1) form.tags.splice(idx, 1)
  else form.tags.push(t)
}

async function submit() {
  if (!form.shopId) { ElMessage.warning('请选择店铺'); return }
  if (!form.title.trim()) { ElMessage.warning('请填写探店标题'); return }
  if (!form.content.trim()) { ElMessage.warning('请填写探店文案'); return }

  submitting.value = true
  try {
    const user = JSON.parse(localStorage.getItem('user'))
    const res = await publishNote({
      userId: user.id,
      shopId: form.shopId,
      title: form.title.trim(),
      content: form.content.trim(),
      stars: form.stars,
      tags: form.tags.join(',')
    })
    if (res.data.success) {
      ElMessage.success('发布成功！')
      router.push('/')
    }
  } catch (e) {
    ElMessage.error('发布失败，请重试')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.publish-form { max-width: 640px; margin: 0 auto; background: #fff; padding: 32px; border-radius: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.04); }
.upload-zone { border: 2px dashed #DDD; border-radius: 10px; padding: 36px; text-align: center; color: #BBB; font-size: 14px; cursor: pointer; transition: border-color 0.2s; }
.upload-zone:hover { border-color: #FF6600; }
.upload-previews { display: flex; gap: 8px; flex-wrap: wrap; justify-content: center; }
.preview-thumb { width: 64px; height: 64px; background: #F5F5F5; border-radius: 6px; display: flex; align-items: center; justify-content: center; font-size: 24px; }
.preview-add { width: 64px; height: 64px; background: #FFF0E6; border-radius: 6px; display: flex; align-items: center; justify-content: center; font-size: 28px; color: #FF6600; }

.star-rating { font-size: 32px; cursor: pointer; display: flex; align-items: center; gap: 8px; }
.star-rating span { color: #E0E0E0; transition: color 0.2s; }
.star-rating span.active { color: #FFB300; }
.star-text { font-size: 14px; color: #666; font-weight: 600; }

.tag-options { display: flex; gap: 8px; flex-wrap: wrap; }
.tag-options span {
  padding: 7px 18px; border-radius: 16px; font-size: 13px;
  background: #F0F0F0; color: #888; cursor: pointer; transition: all 0.2s;
}
.tag-options span.selected { background: #FFF0E6; color: #FF6600; border: 1px solid #FFCCAA; }
</style>