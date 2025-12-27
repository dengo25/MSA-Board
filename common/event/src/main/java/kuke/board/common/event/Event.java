package kuke.board.common.event;

import kuke.board.common.dataserializer.DataSerializer;
import lombok.Getter;

// 통신을 위한 클래스
@Getter
public class Event<T extends EventPayload> {
    private Long eventId;
    private EventType type;
    private T payload; //어떤 데이터를 갖고 있는지 나타내는 필드

    public static Event<EventPayload> of(Long eventId, EventType type, EventPayload payload) {
        Event<EventPayload> event = new Event<>();
        event.eventId = eventId;
        event.type = type;
        event.payload = payload;
        return event;
    }

    // event 클래스를 json으로 변경
    public String toJson() {
        return DataSerializer.serialize(this);
    }

    // json 데이터를 받아서 event 객체로 변환
    public static Event<EventPayload> fromJson(String json) {
        EventRaw eventRaw = DataSerializer.deserialize(json, EventRaw.class);
        if (eventRaw == null) {
            return null;
        }
        Event<EventPayload> event = new Event<>();
        event.eventId = eventRaw.getEventId();
        event.type = EventType.from(eventRaw.getType());
        event.payload = DataSerializer.deserialize(eventRaw.getPayload(), event.type.getPayloadClass());
        return event;
    }

    //type에 따라서 payload가 달라지기 때문에 확인하기 위한 클래스
    @Getter
    private static class EventRaw {
        private Long eventId;
        private String type;
        private Object payload;
    }
}
