from fontTools.ttLib import TTFont
from fontTools.subset import Subsetter, Options

def read_subset_characters(subset_file):
    """读取 subset.txt 字符集"""
    try:
        with open(subset_file, 'r', encoding='utf-8') as f:
            characters = f.read().strip()
            return set(characters)
    except FileNotFoundError:
        print(f"Error: File '{subset_file}' not found.")
        return set()

def compress_ttf(input_ttf, output_ttf, subset_chars):
    """TTF crop"""
    font = TTFont(input_ttf)

    # 设置子集选项和子集对象
    options = Options()
    options.desubroutinize = True
    subsetter = Subsetter(options=options)

    # 保留字符
    subsetter.populate(text="".join(subset_chars))

    # 子集化
    subsetter.subset(font)

    font.save(output_ttf)
    font.close()

    print(f"Compressed font saved as '{output_ttf}'.")

if __name__ == "__main__":
    # 输入/输出文件和字符子集文件
    input_ttf = "./develop-tool/font-crop/meta-normal.ttf"
    output_ttf = "./develop-tool/font-crop/meta-normal-crop.ttf"
    subset_file = "./develop-tool/font-crop/subset.txt"

    subset_chars = read_subset_characters(subset_file)

    if subset_chars:
        compress_ttf(input_ttf, output_ttf, subset_chars)
    else:
        print("No characters found in subset.txt.")
