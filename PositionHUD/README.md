# PositionHUD

Minecraft Java版で統合版のように画面に座標を表示するプラグインです。

## 機能

- 画面下部（アクションバー）にXYZ座標をリアルタイム表示
- `/poshud` コマンドで表示のON/OFF切り替え
- デフォルトでON
- 参加中のプレイヤー全員に自動で表示

## コマンド

| コマンド | 説明 |
|---------|------|
| `/poshud` | 座標表示のON/OFFを切り替え |

## 表示例

```
X: 123  Y: 64  Z: -456
```

## インストール

1. `build/libs/PositionHUD-<日付>.jar` をサーバーの `plugins` フォルダにコピー
2. サーバーを再起動

## ビルド方法

```bash
cd PositionHUD
./gradlew build
```

生成されたjarファイルは `build/libs/` に保存されます。

## 対応バージョン

- Minecraft 1.21+
- Paper サーバー

## ライセンス

このプラグインは自由に使用・改変できます。
