package com.animalleague.april.unit.common;

import com.animalleague.april.common.domain.CharacterAssetStatus;
import com.animalleague.april.common.domain.DialogueTriggerType;
import com.animalleague.april.common.domain.Gender;
import com.animalleague.april.common.domain.PersonalityType;
import com.animalleague.april.common.domain.ScriptLineKind;
import com.animalleague.april.common.domain.SpeakerRole;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CommonDomainEnumUnitTest {

    @Test
    void documentedEnumValuesAreParsedAsExpected() {
        assertThat(Gender.fromValue("male")).isEqualTo(Gender.MALE);
        assertThat(CharacterAssetStatus.fromValue("ready")).isEqualTo(CharacterAssetStatus.READY);
        assertThat(PersonalityType.fromValue("english_mix")).isEqualTo(PersonalityType.ENGLISH_MIX);
        assertThat(DialogueTriggerType.fromValue("study_end")).isEqualTo(DialogueTriggerType.STUDY_END);
        assertThat(ScriptLineKind.fromValue("stage_direction")).isEqualTo(ScriptLineKind.STAGE_DIRECTION);
        assertThat(SpeakerRole.fromValue("professor")).isEqualTo(SpeakerRole.PROFESSOR);
    }
}

