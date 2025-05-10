package kz.sdu.booking.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
public class ListResponse<T> {
    private List<T> data;
    private long count;

    public static <T> ListResponse<T> empty() {
        return new ListResponse<>(Collections.emptyList(), 0);
    }
}
