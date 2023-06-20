package ian.jungmann.ij0292.mapper;

import ian.jungmann.ij0292.dto.ToolRentalAgreementRequestDto;
import ian.jungmann.ij0292.dto.ToolRentalAgreementResponseDto;
import ian.jungmann.ij0292.entity.ToolRentalAgreementEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ToolRentalAgreementDtoEntityMapper {

    ToolRentalAgreementEntity mapRequestToEntity(ToolRentalAgreementRequestDto requestDto);
    @Mapping(target="code", source="tool.code")
    @Mapping(target="brand", source="tool.brand")
    @Mapping(target="type", source="tool.type.name")
    @Mapping(target="dailyRentalCharge", source="tool.type.dailyCharge")
    ToolRentalAgreementResponseDto mapEntityToResponse(ToolRentalAgreementEntity entity);

}
