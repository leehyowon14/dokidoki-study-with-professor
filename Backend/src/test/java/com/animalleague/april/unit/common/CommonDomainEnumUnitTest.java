package com.animalleague.april.unit.common;

import java.util.function.Function;

import com.animalleague.april.common.domain.CharacterAssetStatus;
import com.animalleague.april.common.domain.DialogueTriggerType;
import com.animalleague.april.common.domain.Gender;
import com.animalleague.april.common.domain.PersonalityType;
import com.animalleague.april.common.domain.ScriptLineKind;
import com.animalleague.april.common.domain.SpeakerRole;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CommonDomainEnumUnitTest {

    @Test
    void allDocumentedEnumValuesSupportRoundTripParsing() {
        assertRoundTrip(Gender.values(), Gender::fromValue, Gender::value);
        assertRoundTrip(CharacterAssetStatus.values(), CharacterAssetStatus::fromValue, CharacterAssetStatus::value);
        assertRoundTrip(PersonalityType.values(), PersonalityType::fromValue, PersonalityType::value);
        assertRoundTrip(DialogueTriggerType.values(), DialogueTriggerType::fromValue, DialogueTriggerType::value);
        assertRoundTrip(ScriptLineKind.values(), ScriptLineKind::fromValue, ScriptLineKind::value);
        assertRoundTrip(SpeakerRole.values(), SpeakerRole::fromValue, SpeakerRole::value);
    }

    @Test
    void invalidEnumValuesThrowIllegalArgumentException() {
        assertInvalidValueRejected(Gender::fromValue);
        assertInvalidValueRejected(CharacterAssetStatus::fromValue);
        assertInvalidValueRejected(PersonalityType::fromValue);
        assertInvalidValueRejected(DialogueTriggerType::fromValue);
        assertInvalidValueRejected(ScriptLineKind::fromValue);
        assertInvalidValueRejected(SpeakerRole::fromValue);
    }

    private <T> void assertRoundTrip(
        T[] values,
        Function<String, T> parser,
        Function<T, String> serializer
    ) {
        for (T value : values) {
            assertThat(parser.apply(serializer.apply(value))).isEqualTo(value);
        }
    }

    private void assertInvalidValueRejected(Function<String, ?> parser) {
        assertThatThrownBy(() -> parser.apply("unknown"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("unknown");
    }
}
