package com.alura.literalura.service;

public interface IDataConverter {
    <T> T convertData(String json, Class<T> clazz);
}
