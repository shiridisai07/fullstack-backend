package com.workstudy.backend.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.workstudy.backend.model.SystemSetting;

public interface SystemSettingRepository extends JpaRepository<SystemSetting, Long> {

    Optional<SystemSetting> findBySettingKey(String settingKey);

    List<SystemSetting> findByCategory(String category);

    boolean existsBySettingKey(String settingKey);
}
