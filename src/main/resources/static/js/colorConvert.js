function toRGB(number) {
    let color = number;
    const blue = parseInt(color % 0x100, 10);
    color = color >>> 8;
    const green = parseInt(color % 0x100, 10);
    color = color >>> 8;
    const red = parseInt(color % 0x100, 10);
    const alpha = (parseInt(color >>> 8, 10) / 0xFF).toFixed(1);
    return 'rgba(' + red + ', ' + green + ', ' + blue + ')';
}