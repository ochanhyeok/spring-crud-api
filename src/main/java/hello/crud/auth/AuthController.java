package hello.crud.auth;

import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hello.crud.auth.dto.LoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;
	private final SecurityContextRepository securityContextRepository;

	@PostMapping("/login")
	public LoginMember login(
		@RequestBody LoginRequest request,
		HttpServletRequest servletRequest,
		HttpServletResponse servletResponse
	) {
		LoginMember loginMember = authService.login(request);

		servletRequest.getSession(true);
		servletRequest.changeSessionId(); // 세션의 내용은 그대로 두고 id만 새로 발급

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
			loginMember, null, List.of());

		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);

		securityContextRepository.saveContext(context, servletRequest, servletResponse);

		return loginMember;
	}

	@PostMapping("/logout")
	public void logout(HttpServletRequest servletRequest) {
		HttpSession session = servletRequest.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		SecurityContextHolder.clearContext();
	}

	@GetMapping("/me")
	public LoginMember me(@AuthenticationPrincipal LoginMember loginMember) {
		return loginMember;
	}
}
