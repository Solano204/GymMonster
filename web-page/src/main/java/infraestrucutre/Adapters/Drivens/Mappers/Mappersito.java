package infraestrucutre.Adapters.Drivens.Mappers;
import java.util.Map;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Configuration;

import infraestrucutre.Adapters.Drivens.DTOS.DtoDetailUserReciving;
import infraestrucutre.Adapters.Drivens.DTOS.DtoDetailUserSent;
import infraestrucutre.Adapters.Drivens.DTOS.DtoInfoTrainerSent;
import infraestrucutre.Adapters.Drivens.DTOS.DtoMembershipReciving;
import infraestrucutre.Adapters.Drivens.DTOS.DtoMembershipSent;
import infraestrucutre.Adapters.Drivens.DTOS.DtoPoolReciving;
import infraestrucutre.Adapters.Drivens.DTOS.DtoPoolSent;
import infraestrucutre.Adapters.Drivens.DTOS.DtoSpecialtyRecived;
import infraestrucutre.Adapters.Drivens.DTOS.DtoSpecialtySent;
import infraestrucutre.Adapters.Drivens.DTOS.DtoTrainerReciving;


@Configuration
@Mapper
public interface Mappersito {


    Mappersito INSTANCE = Mappers.getMapper(Mappersito.class);
    
    
    DtoTrainerReciving toDtoTrainerReciving (DtoInfoTrainerSent received);
    DtoSpecialtyRecived toDtoSpecialtyRecived (DtoSpecialtySent received);
    DtoMembershipReciving toDtoMembershipReciving (DtoMembershipSent received);
    DtoPoolReciving toDtoPoolReciving (DtoPoolSent received);
    DtoDetailUserReciving toDtoDetailUserReciving (DtoDetailUserSent received);
    
}