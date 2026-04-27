package com.workstudy.backend.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.workstudy.backend.model.SystemSetting;
import com.workstudy.backend.repository.SystemSettingRepository;

@RestController
@RequestMapping("/api/settings")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
@SuppressWarnings("null")
public class SystemSettingController {

    @Autowired
    private SystemSettingRepository settingRepository;

    // ── Get all settings ──
    @GetMapping
    public List<SystemSetting> getAll() {
        List<SystemSetting> all = settingRepository.findAll();
        if (all.isEmpty()) seedDefaults();
        return settingRepository.findAll();
    }

    // ── Get by category ──
    @GetMapping("/category/{category}")
    public List<SystemSetting> getByCategory(@PathVariable String category) {
        return settingRepository.findByCategory(category.toUpperCase());
    }

    // ── Get single setting ──
    @GetMapping("/{key}")
    public SystemSetting getSetting(@PathVariable String key) {
        return settingRepository.findBySettingKey(key)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Setting not found: " + key));
    }

    // ── Upsert setting ──
    @PutMapping("/{key}")
    public SystemSetting updateSetting(@PathVariable String key, @RequestBody Map<String, String> req) {
        SystemSetting setting = settingRepository.findBySettingKey(key).orElseGet(() -> {
            SystemSetting s = new SystemSetting();
            s.setSettingKey(key);
            return s;
        });
        if (req.get("value") != null) setting.setSettingValue(req.get("value"));
        if (req.get("category") != null) setting.setCategory(req.get("category").toUpperCase());
        if (req.get("description") != null) setting.setDescription(req.get("description"));
        if (req.get("valueType") != null) setting.setValueType(req.get("valueType"));
        setting.setUpdatedBy(req.getOrDefault("updatedBy", "admin"));
        return settingRepository.save(setting);
    }

    // ── Bulk update settings ──
    @PutMapping("/bulk")
    public Map<String, String> bulkUpdate(@RequestBody List<Map<String, String>> updates) {
        for (Map<String, String> req : updates) {
            String key = req.get("key");
            if (key == null || key.isBlank()) continue;
            SystemSetting setting = settingRepository.findBySettingKey(key).orElseGet(() -> {
                SystemSetting s = new SystemSetting();
                s.setSettingKey(key);
                return s;
            });
            setting.setSettingValue(req.get("value"));
            if (req.get("category") != null) setting.setCategory(req.get("category").toUpperCase());
            setting.setUpdatedBy(req.getOrDefault("updatedBy", "admin"));
            settingRepository.save(setting);
        }
        return Map.of("message", "Settings updated successfully");
    }

    // ── Delete setting ──
    @DeleteMapping("/{key}")
    public Map<String, String> deleteSetting(@PathVariable String key) {
        SystemSetting s = settingRepository.findBySettingKey(key)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Setting not found"));
        settingRepository.delete(s);
        return Map.of("message", "Setting deleted");
    }

    // ── Seed default settings ──
    private void seedDefaults() {
        List<Object[]> defaults = List.of(
            new Object[]{"platform_name",       "SkillBridge",  "GENERAL",       "STRING",  "Platform display name"},
            new Object[]{"platform_tagline",    "Connect. Learn. Grow.", "GENERAL", "STRING", "Hero tagline"},
            new Object[]{"maintenance_mode",    "false",        "GENERAL",       "BOOLEAN", "Put platform in maintenance mode"},
            new Object[]{"max_job_applications","10",           "GENERAL",       "NUMBER",  "Max applications per student"},
            new Object[]{"require_email_verify","true",         "SECURITY",      "BOOLEAN", "Require email verification on register"},
            new Object[]{"jwt_expiry_hours",    "24",           "SECURITY",      "NUMBER",  "JWT token expiry in hours"},
            new Object[]{"password_min_length", "8",            "SECURITY",      "NUMBER",  "Minimum password length"},
            new Object[]{"two_fa_enabled",      "false",        "SECURITY",      "BOOLEAN", "Enable two-factor authentication"},
            new Object[]{"email_notifications", "true",         "NOTIFICATIONS", "BOOLEAN", "Send email notifications"},
            new Object[]{"sms_notifications",   "false",        "NOTIFICATIONS", "BOOLEAN", "Send SMS notifications"},
            new Object[]{"push_notifications",  "true",         "NOTIFICATIONS", "BOOLEAN", "Send in-app push notifications"},
            new Object[]{"premium_monthly_price","9.99",        "PAYMENTS",      "NUMBER",  "Monthly premium plan price (USD)"},
            new Object[]{"premium_yearly_price", "89.99",       "PAYMENTS",      "NUMBER",  "Yearly premium plan price (USD)"},
            new Object[]{"referral_reward",     "5.00",         "PAYMENTS",      "NUMBER",  "Reward per successful referral (USD)"},
            new Object[]{"feature_leaderboard", "true",         "FEATURES",      "BOOLEAN", "Enable leaderboard module"},
            new Object[]{"feature_referrals",   "true",         "FEATURES",      "BOOLEAN", "Enable referral program"},
            new Object[]{"feature_courses",     "true",         "FEATURES",      "BOOLEAN", "Enable courses module"},
            new Object[]{"feature_certificates","true",         "FEATURES",      "BOOLEAN", "Enable certificates module"},
            new Object[]{"feature_premium",     "true",         "FEATURES",      "BOOLEAN", "Enable premium plans"},
            new Object[]{"job_approval_required","false",       "GENERAL",       "BOOLEAN", "Require admin approval for new jobs"},
            new Object[]{"max_resume_size_mb",  "5",            "GENERAL",       "NUMBER",  "Max resume upload size in MB"}
        );

        for (Object[] row : defaults) {
            String key = (String) row[0];
            if (!settingRepository.existsBySettingKey(key)) {
                SystemSetting s = new SystemSetting();
                s.setSettingKey(key);
                s.setSettingValue((String) row[1]);
                s.setCategory((String) row[2]);
                s.setValueType((String) row[3]);
                s.setDescription((String) row[4]);
                s.setUpdatedBy("system");
                settingRepository.save(s);
            }
        }
    }
}
