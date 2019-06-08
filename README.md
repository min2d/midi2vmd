# midi2vmd
midiファイルからMMD対応vmdファイルを出力するツール

## 使い方
* dist/artifacts/midi2vmd_jar/midi2vmd.jarをダウンロードする。
* java -jar path/to/midi2vmd.jar で実行する。
* midiファイルを選択。
* 反映元(音程/音量)と反映先、反映割合を設定して出力。
* 出力はoutput.vmdです。

## 注意事項
* 入力は__テンポ設定のある1つのトラックのみにしたmidiファイル__ を使用してください。
* テンポ変動は無視されます。最初のテンポで最後まで出力されます。

## 動作環境
jreが必要です。
