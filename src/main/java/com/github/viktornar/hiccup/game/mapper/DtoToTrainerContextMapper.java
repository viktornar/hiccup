package com.github.viktornar.hiccup.game.mapper;

import com.github.viktornar.hiccup.game.character.TrainerContext;
import com.github.viktornar.hiccup.game.dto.Game;
import com.github.viktornar.hiccup.game.dto.Reward;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DtoToTrainerContextMapper {
    DtoToTrainerContextMapper INSTANCE = Mappers.getMapper(DtoToTrainerContextMapper.class);

    @Mapping(source = "gameId", target = "gameId")
    @Mapping(source = "lives", target = "lives")
    @Mapping(source = "gold", target = "gold")
    @Mapping(source = "level", target = "level")
    @Mapping(source = "score", target = "score")
    @Mapping(source = "highScore", target = "highScore")
    TrainerContext gameToContext(Game game);

    @Mapping(source = "lives", target = "lives")
    @Mapping(source = "gold", target = "gold")
    @Mapping(source = "score", target = "score")
    @Mapping(source = "highScore", target = "highScore")
    @Mapping(source = "turn", target = "turn")
    TrainerContext rewardToContext(Reward reward);
}
