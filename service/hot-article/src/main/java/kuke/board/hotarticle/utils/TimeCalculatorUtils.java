package kuke.board.hotarticle.utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class TimeCalculatorUtils {
    public static Duration calculateDurationToMidnight() { //Duration이 ttl이 된다.  자정까지 얼마나 남았는지 구하는 클래스
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = now.plusDays(1).with(LocalTime.MIDNIGHT);
        return Duration.between(now, midnight);
    }
}
