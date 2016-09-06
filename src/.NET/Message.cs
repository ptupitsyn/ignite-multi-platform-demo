class Message
{
    public Message(string author, string text)
    {
        Author = author;
        Text = text;
    }

    public string Author { get; }
    public string Text { get; }
}