# ADR-029: seed の password は `{bcrypt}` prefix 付きハッシュで保存する

- Status: Accepted
- Date: 2026-05-03
- 関連: ADR-019（Flyway による seed 管理） / ADR-021（ログイン認証）

> **決定**: `users.hashed_password` に平文 password を保存しない。seed user も含め、`{bcrypt}` prefix 付きの BCrypt ハッシュ文字列を保存し、照合には `PasswordEncoderFactories.createDelegatingPasswordEncoder()` で作成した `PasswordEncoder` を使う。

## 背景

ログイン確認用の seed user を Flyway migration で投入する（ADR-019）にあたり、password をどの形式で保存するかを決める必要があった。

保存形式は照合側の実装と対になっており、片方だけ決めると噛み合わない。また、seed に入れた形式はその後のすべての user に引き継がれるため、後から変えると全ユーザーの再ハッシュが必要になる。

## 理由

- `hashed_password` という列名に平文を入れると、列名と実際の中身が矛盾する。後から見た人が平文だと気づけない
- `{bcrypt}` のようなアルゴリズム識別子を hash 文字列自体に含めておくと、将来ハッシュ方式を変更するときに、既存の hash を読みながら新規保存だけ新方式にできる。prefix がないと、保存済みの hash がどの方式で作られたか判別できず、一括移行しか選べなくなる
- 照合を `PasswordEncoder#matches` に任せるため、seed と実装で同じ仕組みを使うことになり、seed だけ別扱いにならない

## 検討した他の案

- **案A: 平文 password を seed に保存する** — 列名と中身が矛盾し、照合実装ともつながらない
- **案B: `BCryptPasswordEncoder` を直接 Bean 化し、prefix なしの `$2a$...` だけを保存する** — 最小実装としては成立するが、hash 文字列からアルゴリズムを判別できない。ハッシュ方式を変更する段階で移行の選択肢が狭まる

## 受け入れる制約

- seed password を更新するときに、平文とハッシュ値を取り違えないよう管理する必要がある
- ハッシュ値を seed SQL に直接書くため、確認用アカウントの password が実質的に固定される
- 動作確認用アカウントの平文 password はリポジトリ外で管理する必要がある

## 見直す条件

- 外部 IdP を導入し、アプリ側で password を保持しない方式へ移行する場合
- BCrypt 以外のハッシュ方式へ移行する場合
- 開発用 seed とテスト用 seed を分ける必要が出た場合
