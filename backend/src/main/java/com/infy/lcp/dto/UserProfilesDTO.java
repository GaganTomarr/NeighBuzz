package com.infy.lcp.dto;

import com.infy.lcp.entity.ContactType;
import com.infy.lcp.entity.ProfileVisibility;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserProfilesDTO {

	@NotNull(message = "{profile.profileId.notNull}")
    private Integer profileId;

    @NotNull(message = "{profile.userDTO.notNull}")
    @Valid
    private UsersDTO userDTO;

    @Size(max = 100, message = "{profile.displayName.size}")
    private String displayName;

    private String profilePicture;

    @Size(max = 1000, message = "{profile.bio.size}")
    private String bio;  // Text field

    @Size(max = 100, message = "{profile.location.size}")
    private String location;

    @NotNull(message = "{profile.profileVisibility.notNull}")
    private ProfileVisibility profileVisibility;

    @NotNull(message = "{profile.contactType.notNull}")
    private ContactType contactType;
}
