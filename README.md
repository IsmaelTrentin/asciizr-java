# Asciizr - Java

Old Java homework that needed to create ascii art from bmp images.

## Usage

```shell
java Asciizr <bmp_path> [-H] [-o <out_file_path>] [-c <char_map>] [-i]
```

Example:

```shell
java src/Asciizr testimgs/pepe.bmp -H -c "[#,,\., ]"
```

Output to file:

```shell
java src/Asciizr testimgs/pepe.bmp -H -o pepe-ascii.txt -c "[#,,\., ]"
```

| Argument        | Description                                                                                                               |
| --------------- | ------------------------------------------------------------------------------------------------------------------------- |
| `bmp_path`      | The bmp image file path                                                                                                   |
| `-H`            | Prints some relevant header values                                                                                        |
| `-o`            | Write to file specifier                                                                                                   |
| `out_file_path` | The path to the file on which to write the ascii art. Mandatory if `-o` is specified                                      |
| `-c`            | Custom characters map specifier                                                                                           |
| `char_map`      | The custom characters map to be used to generate the ascii art. Mandatory if `-o` is specified. Example `[@,#,%,!,-,., ]` |
| `-i`            | Inverse brightness values. Must be specified as the last argument                                                         |
