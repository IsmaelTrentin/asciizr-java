# Asciizr - Java

Old Java homework that needed to create ascii art from bmp images.

## Usage

```shell
java Asciizr <bmp_path> [-h] [-o <out_file_path>] [-i]
```

| Argument        | Description                                                                                                               |
| --------------- | ------------------------------------------------------------------------------------------------------------------------- |
| `bmp_path`      | The bmp image file path                                                                                                   |
| `-h`            | Prints some relevant header values                                                                                        |
| `-o`            | Write to file specifier                                                                                                   |
| `out_file_path` | The path to the file on which to write the ascii art. Mandatory if `-o` is specified                                      |
| `-c`            | Custom characters map specifier                                                                                           |
| `char_map`      | The custom characters map to be used to generate the ascii art. Mandatory if `-o` is specified. Example `[@,#,%,!,-,., ]` |
| `-i`            | Inverse brightness values. Must be specified as the last argument                                                         |
