// ユーザー名（メールアドレス）やパスワード、ロールなどのユーザー情報を保持する

package com.example.samuraitravel.security;

import java.util.Collection;

// ユーザーに割り当てられた権限(role)を表すインターフェース
import org.springframework.security.core.GrantedAuthority;
// ユーザー情報を保持するインターフェース(Spring Security が提供)
import org.springframework.security.core.userdetails.UserDetails;

import com.example.samuraitravel.entity.User;


public class UserDetailsImpl implements UserDetails {
	private final User user;
	private final Collection<GrantedAuthority> authorities;
	
	public UserDetailsImpl(User user, Collection<GrantedAuthority> authorities) {
		this.user = user;
		this.authorities = authorities;
	}
	
	// ログイン中のユーザーの会員情報を表示・編集する機能や予約機能で使用
	// メソッドは独自に定義したもの
	public User getUser() {
		return user;
	}
	
	// ハッシュ化済みのパスワードを返す
	
	// @Override：抽象メソッドをオーバーライド
	// 	可読性の向上、コンパイル時にエラーを出せる、親クラス・インターフェースのメソッド変更時にsubクラスの変更の必要性に気付ける
	@Override
	public String getPassword() {
		return user.getPassword();
	}
	
	// ログイン時に利用するユーザー名（メールアドレス）を返す
	@Override
	public String getUsername() {
		return user.getEmail();
	}
	
	// 権限を返す
	@Override
	// <? extends GrantedAuthority>：GrantedAuthorityまたはそのサブタイプ全て
	public Collection<? extends GrantedAuthority> getAuthorities(){
		return authorities;
	}
	
	// 以下の情報が全てtrueであれば、ログインが成功する
	
	// アカウントが期限切れでなければtrueを返す
	@Override
	public boolean isAccountNonExpired() {
		// 今回のアプリで設定していないので、trueを返している
		return true;
	}
	
	// ユーザーのパスワードがロックされていなければtrueを返す
	@Override
	public boolean isAccountNonLocked() {
		// 今回のアプリで設定していないので、trueを返している
		return true;
	}
	
	// ユーザーのパスワードが期限切れでなければtrueを返す
	@Override
	public boolean isCredentialsNonExpired() {
		// 今回のアプリで設定していないので、trueを返している
		return true;
	}
	
	// ユーザーの有効性チェック：有効であればtureを返す
	@Override
	public boolean isEnabled() {
		// メール認証で必要となるため、Userテーブルのenabledフィールドの値を返すようにしている
		return user.getEnabled();
	}

}
