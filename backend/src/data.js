export const products = [
  {
    id: "prod_1",
    title: "重磅埃及短袖",
    description: "高支棉面料，适合夏季通勤和日常穿搭。",
    priceCents: 9900,
    originalPriceCents: 19900,
    status: "ON_SALE",
    coverUrl: "https://images.unsplash.com/photo-1714070700737-24acfe6b957c",
    salesCount: 8312,
    tags: ["直播热卖", "限时 5 折", "低库存"],
    guaranteeTags: ["7 天无理由", "极速退款", "正品保障"],
    couponText: "满 119 减 20",
    lowStockThreshold: 5,
    skus: [
      { id: "sku_1", specs: { 颜色: "红色", 尺寸: "M" }, priceCents: 9900, stock: 10, skuCode: "SKU001" },
      { id: "sku_2", specs: { 颜色: "红色", 尺寸: "L" }, priceCents: 9900, stock: 3, skuCode: "SKU002" },
      { id: "sku_3", specs: { 颜色: "白色", 尺寸: "M" }, priceCents: 10900, stock: 0, skuCode: "SKU003" }
    ]
  },
  {
    id: "prod_2",
    title: "线下大标品短裤",
    description: "宽松休闲，适合夏季多场景穿搭。",
    priceCents: 12900,
    originalPriceCents: 16900,
    status: "ON_SALE",
    coverUrl: "https://images.unsplash.com/photo-1740512922260-543b1b83c986",
    salesCount: 2460,
    tags: ["家人福利", "大标品"],
    guaranteeTags: ["运费险", "7 天无理由"],
    couponText: "满 129 减 15",
    lowStockThreshold: 5,
    skus: [
      { id: "sku_4", specs: { 颜色: "黑色" }, priceCents: 12900, stock: 20, skuCode: "SKU004" },
      { id: "sku_5", specs: { 颜色: "灰色" }, priceCents: 12900, stock: 2, skuCode: "SKU005" }
    ]
  },
  {
    id: "prod_3",
    title: "便携补光化妆镜",
    description: "三档补光，适合宿舍、旅行和通勤包携带。",
    priceCents: 6900,
    originalPriceCents: 9900,
    status: "ON_SALE",
    coverUrl: "https://picsum.photos/400/400?random=3",
    salesCount: 12980,
    tags: ["猜你喜欢", "送收纳袋"],
    guaranteeTags: ["坏损包退", "极速退款"],
    couponText: "第二件 8 折",
    lowStockThreshold: 5,
    skus: [
      { id: "sku_6", specs: { 颜色: "奶油白" }, priceCents: 6900, stock: 6, skuCode: "SKU006" },
      { id: "sku_7", specs: { 颜色: "樱花粉" }, priceCents: 6900, stock: 0, skuCode: "SKU007" }
    ]
  }
];

export const coupons = [
  { id: "coupon_1", title: "直播间专享券", thresholdCents: 9900, discountCents: 2000, expiresInText: "今日 23:59 过期", productIds: [] },
  { id: "coupon_2", title: "穿搭组合券", thresholdCents: 18800, discountCents: 3500, expiresInText: "还剩 2 天", productIds: ["prod_1", "prod_2"] }
];

export const defaultAddress = {
  id: "addr_1",
  receiverName: "Derek Wang",
  phone: "123-0000-0000",
  province: "广东省",
  city: "深圳市",
  district: "南山区",
  detail: "科技园南区 Street 123",
  isDefault: true
};
