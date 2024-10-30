package infraestrucutre.Adapters.Drivens.Mappers;
import java.util.Map;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Configuration;

import infraestrucutre.Adapters.Drivens.DTOS.DtoDetailUserSent;
import infraestrucutre.Adapters.Drivens.Entities.DetailUser;


@Configuration
@Mapper
public interface Mappersito {


    Mappersito INSTANCE = Mappers.getMapper(Mappersito.class);
    DetailUser toDtoDetailUser(DtoDetailUserSent received);
   
}
