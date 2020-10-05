using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class tags : MonoBehaviour
{   // This class assigns multiple tags to an object.
    private List<string> tagList = new List<string>();

    public bool has(string wantedTag)
    {
        return tagList.Contains(wantedTag);
    }

    public void add(string tagName)
    {
        if(!has(tagName))
            tagList.Add(tagName);
    }

    public void remove(string tagName)
    {
        if(has(tagName))
        {
            tagList.Remove(tagName);
        }
    }
}
