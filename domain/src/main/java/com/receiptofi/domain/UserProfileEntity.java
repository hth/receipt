package com.receiptofi.domain;

import com.google.common.collect.Lists;

import com.receiptofi.domain.types.ProviderEnum;
import com.receiptofi.domain.types.UserLevelEnum;
import com.receiptofi.utils.CommonUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.social.facebook.api.EducationExperience;
import org.springframework.social.facebook.api.Reference;
import org.springframework.social.facebook.api.WorkEntry;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.validation.constraints.NotNull;

/**
 * User: hitender
 * Date: 4/13/14 2:19 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document (collection = "USER_PROFILE")
@CompoundIndexes ({
        @CompoundIndex (name = "user_profile_rid_puid_pid_em_idx", def = "{'RID': -1, 'PUID': -1, 'PID': 1, 'EM' : 1}", unique = true),
        @CompoundIndex (name = "user_profile_em_idx", def = "{'EM': 1}", unique = true)
})
public class UserProfileEntity extends BaseEntity {

    @NotNull
    @Field ("RID")
    private String receiptUserId;

    /** Provider User Id matches id's from social provider. */
    @NotNull
    @Field ("PUID")
    private String providerUserId;

    @NotNull
    @Field ("PID")
    private ProviderEnum providerId;

    @Field ("UN")
    private String username;

    @Field ("N")
    private String name;

    @Field ("FN")
    private String firstName;

    @Field ("MN")
    private String middleName;

    @Field ("LN")
    private String lastName;

    @Field ("GE")
    private String gender;

    @Field ("LO")
    private Locale locale;

    /** Profile URL. */
    @Field ("URL")
    private String link;

    @Field ("WS")
    private String website;

    @Field ("EM")
    private String email;

    @Field ("TP_ID")
    private String thirdPartyId;

    @Field ("TZ")
    private Float timezone;

    @Field ("UT")
    private Date updatedTime;

    @Field ("VR")
    private Boolean verified;

    @Field ("AB")
    private String about;

    @Field ("BI")
    private String bio;

    @Field ("BD")
    private String birthday;

    @Field ("LK")
    private Reference location;

    @Field ("HT")
    private Reference hometown;

    @Field ("II")
    private List<String> interestedIn;

    @Field ("IP")
    private List<Reference> inspirationalPeople;

    @Field ("LA")
    private List<Reference> languages;

    @Field ("SP")
    private List<Reference> sports;

    @Field ("FT")
    private List<Reference> favoriteTeams;

    @Field ("FA")
    private List<Reference> favoriteAthletes;

    @Field ("RL")
    private String religion;

    @Field ("PO")
    private String political;

    @Field ("QU")
    private String quotes;

    @Field ("RS")
    private String relationshipStatus;

    @Field ("SO")
    private Reference significantOther;

    @Field ("WE")
    private List<WorkEntry> work;

    @Field ("EE")
    private List<EducationExperience> education;

    @NotNull
    @Field ("ULE")
    private UserLevelEnum level = UserLevelEnum.USER_PAID;

    @Field ("AD")
    private String address;

    @Field ("CS")
    private String countryShortName;

    @Field ("PH")
    private String phone;

    /** To make bean happy. */
    public UserProfileEntity() {
        super();
    }

    private UserProfileEntity(String email, String firstName, String lastName, String receiptUserId, String birthday) {
        super();
        this.email = email;
        this.firstName = WordUtils.capitalize(firstName);
        this.lastName = WordUtils.capitalize(lastName);
        this.receiptUserId = receiptUserId;
        this.birthday = birthday;
    }

    /**
     * This method is used when the Entity is created for the first time.
     *
     * @param firstName
     * @param lastName
     * @return
     */
    public static UserProfileEntity newInstance(String email, String firstName, String lastName, String receiptUserId, String birthday) {
        return new UserProfileEntity(email, firstName, lastName, receiptUserId, birthday);
    }

    public String getReceiptUserId() {
        return receiptUserId;
    }

    public void setReceiptUserId(String receiptUserId) {
        this.receiptUserId = receiptUserId;
    }

    public String getProviderUserId() {
        return providerUserId;
    }

    public void setProviderUserId(String providerUserId) {
        this.providerUserId = providerUserId;
    }

    public ProviderEnum getProviderId() {
        return providerId;
    }

    public void setProviderId(ProviderEnum providerId) {
        this.providerId = providerId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        if (StringUtils.isNotBlank(name)) {
            return name;
        }

        if (StringUtils.isNotBlank(firstName)) {
            if (StringUtils.isNotBlank(lastName)) {
                return StringUtils.trim(firstName + UserAccountEntity.BLANK_SPACE + lastName);
            } else {
                return firstName;
            }
        }

        return providerUserId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = WordUtils.capitalize(firstName);
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = WordUtils.capitalize(lastName);
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getThirdPartyId() {
        return thirdPartyId;
    }

    public void setThirdPartyId(String thirdPartyId) {
        this.thirdPartyId = thirdPartyId;
    }

    public Float getTimezone() {
        return timezone;
    }

    public void setTimezone(Float timezone) {
        this.timezone = timezone;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public Reference getLocation() {
        return location;
    }

    public void setLocation(Reference location) {
        this.location = location;
    }

    public Reference getHometown() {
        return hometown;
    }

    public void setHometown(Reference hometown) {
        this.hometown = hometown;
    }

    public List<String> getInterestedIn() {
        return interestedIn;
    }

    public void setInterestedIn(List<String> interestedIn) {
        this.interestedIn = interestedIn;
    }

    public List<Reference> getInspirationalPeople() {
        return inspirationalPeople;
    }

    public void setInspirationalPeople(List<Reference> inspirationalPeople) {
        this.inspirationalPeople = inspirationalPeople;
    }

    public List<Reference> getLanguages() {
        return languages;
    }

    public void setLanguages(List<Reference> languages) {
        this.languages = languages;
    }

    public List<Reference> getSports() {
        return sports;
    }

    public void setSports(List<Reference> sports) {
        this.sports = sports;
    }

    public List<Reference> getFavoriteTeams() {
        return favoriteTeams;
    }

    public void setFavoriteTeams(List<Reference> favoriteTeams) {
        this.favoriteTeams = favoriteTeams;
    }

    public List<Reference> getFavoriteAthletes() {
        return favoriteAthletes;
    }

    public void setFavoriteAthletes(List<Reference> favoriteAthletes) {
        this.favoriteAthletes = favoriteAthletes;
    }

    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    public String getPolitical() {
        return political;
    }

    public void setPolitical(String political) {
        this.political = political;
    }

    public String getQuotes() {
        return quotes;
    }

    public void setQuotes(String quotes) {
        this.quotes = quotes;
    }

    public String getRelationshipStatus() {
        return relationshipStatus;
    }

    public void setRelationshipStatus(String relationshipStatus) {
        this.relationshipStatus = relationshipStatus;
    }

    public Reference getSignificantOther() {
        return significantOther;
    }

    public void setSignificantOther(Reference significantOther) {
        this.significantOther = significantOther;
    }

    public List<WorkEntry> getWork() {
        return work;
    }

    public void setWork(List<WorkEntry> work) {
        this.work = work;
    }

    public void addWork(WorkEntry work) {
        if (null == this.work) {
            this.work = Lists.newArrayList();
        }
        this.work.add(work);
    }

    public List<EducationExperience> getEducation() {
        return education;
    }

    public void setEducation(List<EducationExperience> education) {
        this.education = education;
    }

    public UserLevelEnum getLevel() {
        return level;
    }

    public void setLevel(UserLevelEnum level) {
        this.level = level;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountryShortName() {
        return countryShortName;
    }

    public void setCountryShortName(String countryShortName) {
        this.countryShortName = countryShortName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        if (StringUtils.isNotBlank(phone)) {
            this.phone = CommonUtil.phoneCleanup(phone);
        }
    }

    public String getPhoneFormatted() {
        if (StringUtils.isNotBlank(phone)) {
            return CommonUtil.phoneFormatter(phone, countryShortName);
        } else {
            return "";
        }
    }

    @Transient
    public String getInitials() {
        String name = getName();
        if (!StringUtils.isBlank(name)) {
            return WordUtils.initials(name);
        } else {
            return WordUtils.initials(getEmail()) + "@";
        }
    }
}
