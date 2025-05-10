package kz.sdu.booking.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStatisticsDto {
    private int hoursInLibrary;        // сколько часов всего
    private int minutesInLibrary;      // сколько минут всего
    private int bookingDaysInMonth;    // количество дней бронирования за месяц
    private int recordDay;             // рекорд в днях за одно бронирование
    private int recordHours;           // рекорд в часах за одно бронирование
}

