// ユーザー情報の取得。
// UserDetailsImplクラスのインスタンスを生成など、ビジネスロジックを設定


package com.example.samuraitravel.security;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.repository.UserRepository;

@Service
// UserDetailsServiceインターフェースを実装する
public class UserDetailsServiceImpl implements UserDetailsService{
	
	// フィールド変数　設定
	private final UserRepository userRepository;
	// コンストラクタ
	public UserDetailsServiceImpl(UserRepository userRepository){
		
		this.userRepository = userRepository;
	}
	
	@Override
	// UserDetailsServiceインターフェースのloadUserByUsername()メソッドを実装
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
		try {
			// フォームから送信されたメールアドレスに一致するユーザーを取得する
			User user = userRepository.findByEmail(email);
			// そのユーザーのRoleを取得する
			String userRoleName = user.getRole().getName();
			

			// GrantedAuthority：セキュリティフレームワークで使用される、認可された権限を表すインターフェース
			// Collectionクラス型のauthoritiesに初期化したArrayListを設定し、GrantedAuthorityオブジェクト格納のための準備
			Collection<GrantedAuthority> authorities = new ArrayList<>();
			
			// Array配列に情報を格納
			// authoritiesというArrayListに新しいGrantedAuthorityオブジェクトを追加。
			// new SimpleGrantedAuthority(userRoleName)：userRoleNameを権限名として持つ新しいSimpleGrantedAuthorityオブジェクトを生成
			authorities.add(new SimpleGrantedAuthority(userRoleName));
			
			// 取得したユーザー情報とRole情報をUserDetailsImplクラスのコンストラクタに返し、インスタンスを生成
			return new UserDetailsImpl(user, authorities);
			
		}catch(Exception e) {
			throw new UsernameNotFoundException("ユーザーが見つかりませんでした。");
		}
	}
	
}
