import os
from PIL import Image

def resize_images_in_folder(folder_path, target_height):
    for filename in os.listdir(folder_path):
        if filename.lower().endswith('.png'):
            image_path = os.path.join(folder_path, filename)
            
            # 打开图片
            img = Image.open(image_path)
            
            # 计算新的宽度，保持宽高比
            width, height = img.size
            target_width = int(width * target_height / height)
            
            # 调整图片大小
            img_resized = img.resize((target_width, target_height))
            
            new_image_path = os.path.join(os.getcwd() + "/develop-tool/img-crop/output", filename)
            img_resized.save(new_image_path)

folder_path = './assets/img'  # 文件夹路径
target_height = 64  # 目标高度

resize_images_in_folder(folder_path, target_height)
