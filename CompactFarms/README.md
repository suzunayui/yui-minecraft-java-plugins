# CompactFarms

 シュルカーボックスに自動的に資源を生成するMinecraftプラグインです。

## 機能

- カスタムクラフトレシピでCompactFarmsを作成
- コンテナを設置するだけで自動的に資源が生成されます
- トラップ作成の手間やサーバー負荷を軽減
- 資源の種類・生成間隔・上限数を設定で変更可能

## クラフトレシピ

### 鉄CompactFarms（白いシュルカーボックス）
鉄インゴットが1分ごとに1個ずつ自動生成されます。

```
I I I
I C I
I I I
```
- I = 鉄インゴット
- C = チェスト

### エメラルドCompactFarms（緑のシュルカーボックス）
エメラルドが1分ごとに1個ずつ自動生成されます。

```
E E E
E C E
E E E
```
- E = エメラルド
- C = チェスト

### 火薬CompactFarms（灰色のシュルカーボックス）
火薬が1分ごとに1個ずつ自動生成されます。

```
G G G
G C G
G G G
```
- G = 火薬
- C = チェスト

## 生成される資源

| 資源 | 量 | 間隔 | 上限 |
|------|-----|------|------|
| 鉄インゴット | 1個 | 60秒 | シュルカーボックス満杯まで |
| エメラルド | 1個 | 60秒 | シュルカーボックス満杯まで |
| 火薬 | 1個 | 60秒 | シュルカーボックス満杯まで |

## コマンド

- `/compactfarms reload` - 設定を再読み込みします
- `/compactfarms info` - 自分のコンテナ登録数を確認します

## 権限

- `compactfarms.admin` - 管理者用コマンドの使用権限（デフォルト: OP）

## 設定

`plugins/CompactFarms/config.yml` で以下を調整できます：

- `resources` - 生成される資源の設定（量、間隔、上限）
- `global.max-per-type` - プレイヤーごとの種類ごとの最大コンテナ数（デフォルト: 1）

## インストール

1. `build/libs/CompactFarms-1.0.0.jar` をサーバーの `plugins` フォルダにコピー
2. サーバーを再起動
3. `plugins/CompactFarms/config.yml` を必要に応じて編集

## ビルド方法

```bash
cd CompactFarms
./gradlew build
```

生成されたjarファイルは `build/libs/` に保存されます。

## 対応バージョン

- Minecraft 1.21+
- Paper サーバー

## ライセンス

このプラグインは自由に使用・改変できます。
