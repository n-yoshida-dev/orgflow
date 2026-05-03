# Docker / Docker Compose チートシート

## 1. よく使うコマンド

| 目的                     | コマンド                       | メモ                                                 |
| ------------------------ | ------------------------------ | ---------------------------------------------------- |
| 起動する                 | `docker compose up -d`         | 裏側で起動する                                       |
| 停止・削除する           | `docker compose down`          | コンテナとネットワークを削除。DBデータ用volumeは残る |
| 状態を見る               | `docker compose ps`            | まずこれで確認                                       |
| ログを見る               | `docker compose logs -f`       | エラー調査で使う                                     |
| 特定サービスのログを見る | `docker compose logs -f api`   | `api` はサービス名                                   |
| 再ビルドして起動         | `docker compose up -d --build` | Dockerfileや依存関係を変えた後                       |
| APIだけ再起動            | `docker compose restart api`   | `api` はサービス名                                   |
| DBを初期化して作り直す   | `docker compose down -v`       | DBデータも消えるので注意                             |

---

## 2. 起動・停止

| 目的                     | コマンド                 |
| ------------------------ | ------------------------ |
| 通常起動                 | `docker compose up -d`   |
| ログを表示しながら起動   | `docker compose up`      |
| 停止だけする             | `docker compose stop`    |
| 停止したものを再開する   | `docker compose start`   |
| 停止して削除する         | `docker compose down`    |
| DBデータも含めて削除する | `docker compose down -v` |

---

## 3. 状態確認

| 目的                             | コマンド            |
| -------------------------------- | ------------------- |
| Composeのサービス状態を見る      | `docker compose ps` |
| Docker全体の起動中コンテナを見る | `docker ps`         |
| 停止中も含めて見る               | `docker ps -a`      |
| イメージ一覧を見る               | `docker images`     |
| volume一覧を見る                 | `docker volume ls`  |

---

## 4. ログ確認

| 目的                   | コマンド                             |
| ---------------------- | ------------------------------------ |
| 全サービスのログを見る | `docker compose logs`                |
| ログを追い続ける       | `docker compose logs -f`             |
| APIだけ見る            | `docker compose logs -f api`         |
| DBだけ見る             | `docker compose logs -f db`          |
| 直近100行だけ見る      | `docker compose logs --tail=100 api` |

---

## 5. コンテナ内で作業する

| 目的                      | コマンド                       |
| ------------------------- | ------------------------------ |
| APIコンテナに入る         | `docker compose exec api bash` |
| bash がない場合           | `docker compose exec api sh`   |
| DBコンテナに入る          | `docker compose exec db bash`  |
| APIコンテナでコマンド実行 | `docker compose exec api ls`   |

---

## 6. PostgreSQLを触る

サービス名が `db`、ユーザー名が `orgflow`、DB名が `orgflow` の場合。

| 目的               | コマンド                                            |
| ------------------ | --------------------------------------------------- |
| psqlに入る         | `docker compose exec db psql -U orgflow -d orgflow` |
| DB一覧を見る       | `\l`                                                |
| テーブル一覧を見る | `\dt`                                               |
| テーブル定義を見る | `\d [テーブル名]`                                   |
| psqlを抜ける       | `\q`                                                |

---

## 7. ビルドし直す

| 状況                   | コマンド                           |
| ---------------------- | ---------------------------------- |
| 普通に再ビルドして起動 | `docker compose up -d --build`     |
| キャッシュなしでビルド | `docker compose build --no-cache`  |
| APIだけビルド          | `docker compose build api`         |
| APIだけビルドして起動  | `docker compose up -d --build api` |

---

## 8. トラブル時

### ポートが使われている

```bash
docker ps
docker ps -a
docker compose down
```
