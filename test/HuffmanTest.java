
import org.junit.Assert;
import org.junit.Test;

public class HuffmanTest {


    @Test
    public void testCodingKnowingBytesPattern() {
        //given
        Huffman huf = new Huffman();
        String text = "Engl";

        //when
        String result = huf.codeString(text);

        //then
        Assert.assertEquals("00011110", result);
    }

    @Test
    public void CodingForOneLetter() {
        //given
        Huffman huf = new Huffman();
        String text = "E";

        //when
        String result = huf.codeString(text);

        //then
        Assert.assertEquals("1", result);
    }

    @Test/*then*/(expected = IllegalArgumentException.class)
    public void CodingForEmptyString() {
        //given
        Huffman huf = new Huffman();
        String text = "";

        //when
        String result = huf.codeString(text);
    }


    @Test
    public void testDecoding() {
        //given
        Huffman huf = new Huffman();
        String text = "En gl";

        //when
        String result = huf.decodeString(text);

        //then
        Assert.assertEquals(text, result);
    }

    @Test
    public void shouldDeletePolishLetters() {
        //given
        Huffman huf = new Huffman();
        String text = "E nglł";

        //when
        String result = huf.decodeString(text);

        //then
        Assert.assertEquals("E ngl", result);
    }

    @Test
    public void CodingForStringWithQuotes() {
        //given
        Huffman huf = new Huffman();
        String text = "\"I'm sick\" he said.";

        //when
        String result = huf.decodeString(text);

        //then
        Assert.assertEquals(text, result);
    }

    @Test/*then*/(expected = IllegalArgumentException.class)
    public void CodingStringIsNull() {
        //given
        Huffman huf = new Huffman();
        String text = null;

        //when
        String result = huf.codeString(text);
    }

    @Test/*then*/(expected = IllegalArgumentException.class)
    public void FilePathIsNULL() {
        //given
        Huffman huf = new Huffman();
        String pathToRootDir = null;

        //when
        huf.huffman(pathToRootDir, true);
    }

    @Test/*then*/(expected = RuntimeException.class)
    public void FileWithTextDoesntExist() {
        //given
        Huffman huf = new Huffman();
        String pathToRootDir = "main";

        //when
        huf.huffman(pathToRootDir, true);
    }

    @Test/*then*/(expected = RuntimeException.class)
    public void FileWithTreeDoesntExist() {
        //given
        Huffman huf = new Huffman();
        String pathToRootDir = "main";

        //when
        huf.huffman(pathToRootDir, false);
    }

    @Test/*then*/(expected = RuntimeException.class)
    public void FileWithCodeDoesntExist() {
        //given
        Huffman huf = new Huffman();
        String pathToRootDir = "files/NoCode";

        //when
        huf.huffman(pathToRootDir, false);
    }

    @Test/*then*/(expected = IllegalArgumentException.class)
    public void TextFileIsEmpty() {
        //given
        Huffman huf = new Huffman();
        String pathToRootDir = "files/empty";

        //when
        huf.huffman(pathToRootDir, true);
    }

    @Test/*then*/(expected = RuntimeException.class)
    public void PathIsWrong() {
        //given
        Huffman huf = new Huffman();
        String pathToRootDir = "filesss";

        //when
        huf.huffman(pathToRootDir, true);
    }

    @Test
    public void MainCheck() {
        //given
        Huffman huf = new Huffman();
        Huffman hufde = new Huffman();
        String pathToRootDir = "files";

        //when
        huf.huffman(pathToRootDir, true);
        hufde.huffman(pathToRootDir, false);

        //then
        Assert.assertEquals(huf.getText(), hufde.getText());
    }

    @Test/*then*/(expected = RuntimeException.class)
    public void TreeFileIsEmpty() {
        //given
        Huffman huf = new Huffman();
        String pathToRootDir = "files/empty";

        //when
        huf.huffman(pathToRootDir, false);
    }

    @Test/*then*/(expected = RuntimeException.class)
    public void CodeFileIsEmpty() {
        //given
        Huffman huf = new Huffman();
        String pathToRootDir = "files/empty/emptyCode";

        //when
        huf.huffman(pathToRootDir, false);
    }


}
