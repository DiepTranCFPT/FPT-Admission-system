package com.sba.post.enums;

public enum Category {
    TIN_TUC_CHUNG("TIN TỨC CHUNG"),
    BAO_CHI_NOI_VE_FPTU("BÁO CHÍ NÓI VỀ FPTU"),
    HOC_THUAT("HỌC THUẬT"),
    TRACH_NHIEM_CONG_DONG("TRÁCH NHIỆM CỘNG ĐỒNG"),
    TRAI_NGHIEM_TOAN_CAU("TRẢI NGHIỆM TOÀN CẦU"),
    CUU_SINH_VIEN("Cựu sinh viên"),
    KHOA_HOC("KHÓA HỌC");
    private final String label;

    Category(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
