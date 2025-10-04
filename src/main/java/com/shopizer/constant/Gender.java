package com.shopizer.constant;

public enum Gender {
    MALE("Nam"),
    FEMALE("Nữ");
    String gender;

    Gender(String gender) {
        this.gender = gender;
    }
    public String getGender() {
        return gender;
    }

    public static Gender valueOfGender(String gender){
        for (Gender genderEnum : Gender.values()) {
            if (genderEnum.name().equalsIgnoreCase(gender)) {
                return genderEnum;
            }
        }

        for (Gender genderEnum : Gender.values()) {
            if (genderEnum.getGender().equalsIgnoreCase(gender)) {
                return genderEnum;
            }
        }

        return FEMALE;
    }
}
