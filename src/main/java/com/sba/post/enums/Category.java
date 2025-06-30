package com.sba.post.enums;

public enum Category {
    TIN_TUC_SU_KIEN("Tin tức và sự kiện"),
    NGANH_HOC("Ngành học"),
    TRAI_NGHIEM("Trải Nghiệm Toàn Cầu"),
    SINH_VIEN("Sinh viên"),
    CUU_SINH_VIEN("Cựu sinh viên"),
    LIEN_HE("Liên Hệ");

    private final String label;

    Category(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
