package com.tunahancoban.policy_tracker.mapper;

import com.tunahancoban.policy_tracker.model.entity.Installment;
import com.tunahancoban.policy_tracker.model.indexes.InstallmentIndex;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface InstallmentMapper {

        InstallmentIndex toIndex(Installment installment);

        Installment toEntity(InstallmentIndex installmentIndex);

}
