package study.datajpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;
import java.util.UUID;

@EnableJpaAuditing //꼭 붙여야 작동한다!
//@EnableJpaAuditing(modifyOnCreate = false) 를 하면 최초 등록시에 수정 데이터에 데이터를 반영하지 않음 -> 비추천
@SpringBootApplication
public class DataJpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataJpaApplication.class, args);
	}

	//등록자, 수정자를 처리해주는 AuditorAware 스프링 빈 등록하기
	@Bean
	public AuditorAware<String> auditorProvider() {

		//람다식
//		return () -> Optional.of(UUID.randomUUID().toString());

		/**
		 * BaseEntity의 createdBy, lastModifiedBy가 등록되거나 수정될 때마다 아래 AuditorAware을 호출해서 결과물을 자동으로 꺼내간다.
		 * 값들이 알아서 채워진다!
		 */
		return new AuditorAware<String>() {
			@Override
			public Optional<String> getCurrentAuditor() {
				return Optional.of(UUID.randomUUID().toString()); //이건 일단 랜덤으로 넣은거
				//실무에서는 세션 정보나, 스프링 시큐리티 로그인 정보에서 ID를 받음
			}
		};
	}

}
