package study.datajpa.dto;

public class UsernameOnlyDto {

    private final String username;

    public UsernameOnlyDto(String username) { //파라미터 이름을 가지고 분석을 해서
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
