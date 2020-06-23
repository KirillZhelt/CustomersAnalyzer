package dev.kirillzhelt.customers.dto;

public class DataResponseDTO<T> {

    private T data;

    public DataResponseDTO(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "DataResponseDTO{" +
            "data=" + data +
            '}';
    }
}
