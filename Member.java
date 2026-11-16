package com.mycompany.jacinto_bookbridge_tsa3;

/**
 *
 * @author Gail Jacinto
 */
import java.time.LocalDate;
import java.util.Objects;

public class Member {
    private String memberId;
    private String name;
    private String email;
    private MembershipType membershipType;
    private LocalDate registrationDate;

    public Member(String memberId, String name, String email, MembershipType membershipType) {
        this.memberId = memberId;
        this.name = name;
        this.email = email;
        this.membershipType = membershipType;
        this.registrationDate = LocalDate.now();
    }

    public String getMemberId() { return memberId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public MembershipType getMembershipType() { return membershipType; }
    public LocalDate getRegistrationDate() { return registrationDate; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Member)) return false;
        Member member = (Member) o;
        return memberId.equals(member.memberId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId);
    }

    @Override
    public String toString() {
        return String.format("Member[ID=%s, Name='%s', Type=%s]", memberId, name, membershipType);
    }
}