package ku_rum.backend.domain.notice.domain.value.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ku_rum.backend.domain.notice.domain.value.ViewCount;

@Converter
public class ViewCountConverter implements AttributeConverter<ViewCount, Long> {
    @Override
    public Long convertToDatabaseColumn(ViewCount attribute) {
        return attribute == null ? 0 : attribute.getCount();
    }

    @Override
    public ViewCount convertToEntityAttribute(Long dbData) {
        return dbData == null ? new ViewCount(0) : new ViewCount(dbData);
    }
}
