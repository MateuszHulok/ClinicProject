package com.clinic.project1.mapper;

import com.clinic.project1.common.Disease;
import com.clinic.project1.common.Specialization;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

public class SpecializationMapper {

    private static final Map<Disease, Specialization> diseaseToSpecializationMap = new EnumMap<>(Disease.class);

    static {
        diseaseToSpecializationMap.put(Disease.FLU, Specialization.FAMILY_MEDICINE);
        diseaseToSpecializationMap.put(Disease.COVID_19, Specialization.FAMILY_MEDICINE);
        diseaseToSpecializationMap.put(Disease.TONSILLITIS, Specialization.FAMILY_MEDICINE);
        diseaseToSpecializationMap.put(Disease.BRONCHITIS, Specialization.PULMONOLOGY);
        diseaseToSpecializationMap.put(Disease.PNEUMONIA, Specialization.PULMONOLOGY);
        diseaseToSpecializationMap.put(Disease.CHICKENPOX, Specialization.PEDIATRICS);
        diseaseToSpecializationMap.put(Disease.RUBELLA, Specialization.PEDIATRICS);
        diseaseToSpecializationMap.put(Disease.MUMPS, Specialization.PEDIATRICS);
        diseaseToSpecializationMap.put(Disease.MEASLES, Specialization.PEDIATRICS);
        diseaseToSpecializationMap.put(Disease.SCARLET_FEVER, Specialization.PEDIATRICS);
    }

    public static Specialization getRequiredSpecializationForDisease(Disease disease) {
        Specialization specialization = diseaseToSpecializationMap.get(disease);
        if (specialization == null) {
            throw new IllegalArgumentException("No specialization found for disease " + disease);
        }
        return specialization;
    }
}
