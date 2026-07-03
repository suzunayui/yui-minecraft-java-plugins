# ContainerSearch

近くのコンテナの中身を検索するMinecraftプラグインです。

## 機能

- `/cs <アイテム名>` で近くのコンテナ内を検索
- 検索範囲: x±10, z±10, y-1〜+5
- チェスト、タル、シュルカーボックス等すべて対応
- シュルカーボックス内のネストされたアイテムも再帰的に検索
- タブ補完でアイテム名一覧を表示

## コマンド

| コマンド | 説明 |
|---------|------|
| `/cs <アイテム名>` | 近くのコンテナからアイテムを検索 |
| `/cs` | ヘルプを表示 |

## 検索結果表示例

```
=== ダイヤモンド の検索結果 (2件) ===
チェスト > シュルカーボックス x10 y64 z20 (32個)
タル x15 y63 z18 (5個)
```

## 設定

`plugins/ContainerSearch/config.yml` で検索範囲を調整できます：

```yaml
search:
  range-x: 10
  range-z: 10
  range-y-min: -1
  range-y-max: 5
```

## インストール

1. `build/libs/ContainerSearch-<日付>.jar` をサーバーの `plugins` フォルダにコピー
2. サーバーを再起動

## ビルド方法

```bash
cd ContainerSearch
./gradlew build
```

生成されたjarファイルは `build/libs/` に保存されます。

## 対応バージョン

- Minecraft 1.21+
- Paper サーバー

## ライセンス

このプラグインは自由に使用・改変できます。
