import torch
import torch.nn as nn
import torch.nn.functional as F

class RLConvNetWithResidual(nn.Module):
    def __init__(self):
        super(RLConvNetWithResidual, self).__init__()
        
        # 卷积层1: 输入2通道，输出32个特征图，卷积核大小3x3
        self.conv1 = nn.Conv2d(in_channels=2, out_channels=32, kernel_size=3, stride=1, padding=1)
        
        # 卷积层2: 输入32通道，输出64个特征图，卷积核大小3x3
        self.conv2 = nn.Conv2d(in_channels=32, out_channels=64, kernel_size=3, stride=1, padding=1)
        
        # 卷积层3: 输入64通道，输出128个特征图，卷积核大小3x3
        self.conv3 = nn.Conv2d(in_channels=64, out_channels=128, kernel_size=3, stride=1, padding=1)

        # 1x1卷积层：通过1x1卷积压缩通道数量，减少参数量
        self.conv4 = nn.Conv2d(in_channels=128, out_channels=32, kernel_size=1, stride=1, padding=0)

        # 残差连接：将128通道和32通道的卷积结果相加，需要先通过1x1卷积匹配通道
        self.residual1x1 = nn.Conv2d(in_channels=128, out_channels=32, kernel_size=1, stride=1, padding=0)

        # 最终输出：使用一个单独的卷积来生成最终的一个实数输出
        self.output_conv = nn.Conv2d(in_channels=32, out_channels=1, kernel_size=1, stride=1, padding=0)
    
    def forward(self, x):
        # 卷积+ReLU+池化1
        x1 = F.relu(self.conv1(x))
        x1 = F.max_pool2d(x1, 2)
        
        # 卷积+ReLU+池化2
        x2 = F.relu(self.conv2(x1))
        x2 = F.max_pool2d(x2, 2)
        
        # 卷积+ReLU+池化3
        x3 = F.relu(self.conv3(x2))
        x3 = F.max_pool2d(x3, 2)

        # 1x1卷积（压缩通道数）
        x4 = F.relu(self.conv4(x3))
        
        # 残差连接
        x_residual = F.relu(self.residual1x1(x3))
        x_residual = x_residual + x4
        
        # 最后输出层，得到一个标量输出
        output = self.output_conv(x_residual)
        
        # 将输出展平并返回结果
        return output.view(-1)
        

# 创建模型实例
model = RLConvNetWithResidual()

# 打印模型架构
print(model)
