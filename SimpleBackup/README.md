# SimpleBackup

Minecraftサーバーの自動バックアッププラグインです。

## 機能

- サーバー起動時に自動バックアップ
- プレイヤーが1人以上いる場合、1時間ごとに自動バックアップ
- 手動バックアップ（`/backup` コマンド）
- ワールドフォルダをコピー後にzip圧縮（非同期処理でサーバー負荷を軽減）
- 圧縮後のフォルダは自動削除（設定で変更可能）
- 各日最初のバックアップは無期限に保持し、それ以外は直近12時間分を保持

## バックアップ内容

- すべてのワールドフォルダをコピー後にzip圧縮
- 保存場所: `plugins/SimpleBackup/backups/`
- ファイル名形式: `2026-07-03_09-00.zip`（年月日_時分）

## 設定

`plugins/SimpleBackup/config.yml` で以下を調整できます：

```yaml
# バックアップ後に元のフォルダを削除するかどうか
# true: zip圧縮後にフォルダを削除（推奨）
# false: フォルダとzipの両方を保持
delete-after-compress: true

# 日次分以外のバックアップを保持する時間
hourly-retention-hours: 12
```

各日で最初に成功したバックアップは、この保持時間を過ぎても自動削除されません。
不要になった日次バックアップは `plugins/SimpleBackup/backups/` から手動で削除してください。

## コマンド

| コマンド | 説明 |
|---------|------|
| `/backup` | 手動でバックアップを作成（OP権限必要） |

## インストール

1. `build/libs/SimpleBackup-<日付>.jar` をサーバーの `plugins` フォルダにコピー
2. サーバーを再起動

## ビルド方法

```bash
cd SimpleBackup
./gradlew build
```

生成されたjarファイルは `build/libs/` に保存されます。

## 対応バージョン

- Minecraft 1.21+
- Paper サーバー

## ライセンス

このプラグインは自由に使用・改変できます。
