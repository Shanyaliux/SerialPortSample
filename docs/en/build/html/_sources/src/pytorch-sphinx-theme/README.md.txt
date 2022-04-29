# Introduction
This theme is adapted from PyTorch Sphinx Theme, with more configurations allowed.

## Getting Started
Add dependencies to `requirements.txt`,
```
-e git+https://github.com/open-mmlab/pytorch_sphinx_theme.git#egg=pytorch_sphinx_theme
sphinx_copybutton
```
In `docs/conf.py`:
```python
import pytorch_sphinx_theme

html_theme = 'pytorch_sphinx_theme'
html_theme_path = [pytorch_sphinx_theme.get_html_theme_path()]

# Ignore >>> when copying code
copybutton_prompt_text = r'>>> |\.\.\. '
copybutton_prompt_is_regexp = True
```

### Header Logo
The header logo is OpenMMLab by default. Each repo should customize the logo by overriding the provided css class.

We suggest putting all the html resources to `docs/_static`. First put the logo
to `docs/_static/images/logo.png`, then write the following snippet to 
`docs/_static/css/readthedocs.css`:
```css
.header-logo {
    background-image: url("../images/logo.png");
    background-size: 110px 40px;
    height: 40px;
    width: 110px;
}
```
Here, you are recommended to fix the height to `40px` and scale the width according to the logo's aspect ratio.
The latest thing to do is to tell Sphinx the location of these resources by adding the following lines to `docs/conf.py`:
```python
html_static_path = ['_static']
html_css_files = ['css/readthedocs.css']
```

### Header Customization
This theme variant also allows users to customize the header, such as the logo url and the navigation menu, in a pythonic way. They are all configurable options in `html_theme_options` in `docs/conf.py`.

Here is an example config covering all available options:
```python
html_theme_options = {
    # The target url that the logo directs to. Unset to do nothing
    'logo_url': 'https://mmocr.readthedocs.io/en/latest/',
    # "menu" is a list of dictionaries where you can specify the content and the 
    # behavior of each item in the menu. Each item can either be a link or a
    # dropdown menu containing a list of links.
    'menu': [
        # A link
        {
            'name': 'GitHub',
            'url': 'https://github.com/open-mmlab/'
        }, 
        # A dropdown menu
        {
            'name': 'Projects',
            'children': [
                # A vanilla dropdown item
                {
                    'name': 'MMCV',
                    'url': 'https://github.com/open-mmlab/mmcv',
                },
                # A dropdown item with a description
                {
                    'name': 'MMDetection',
                    'url': 'https://github.com/open-mmlab/mmdetection',
                    'description': 'Object detection toolbox and benchmark'
                },
            ], 
            # Optional, determining whether this dropdown menu will always be
            # highlighted. 
            'active': True,
        },
    ],
    # For shared menu: If your project is a part of OpenMMLab's project and 
    # you would like to append Docs and OpenMMLab section to the right
    # of the menu, you can specify menu_lang to choose the language of
    # shared contents. Available options are 'en' and 'cn'. Any other
    # strings will fall back to 'en'.
    'menu_lang':
    'en',
}
```
# PyTorch Sphinx Theme

Sphinx theme for [PyTorch Docs](https://pytorch.org/docs/master/torch.html) and [PyTorch Tutorials](https://pytorch.org/tutorials) based on the [Read the Docs Sphinx Theme](https://sphinx-rtd-theme.readthedocs.io/en/latest).

## Local Development

Run python setup:

```
python setup.py install
```

and install the dependencies using `pip install -r docs/requirements.txt`

In the root directory install the `package.json`:

```
# node version 8.4.0
yarn install

```

If you have `npm` installed then run:

```
npm install
```

- If you want to see generated documentation for `docs/demo` then create
`.env.json` file and make it empty json file. Means `.env.json file` will
contain

```
{}
```

Run grunt to build the html site and enable live reloading of the demo app at `localhost:1919`:

```
grunt
```

- If you want to specify the project folder (docs or tutorial for which
you want to see docs generated) then you need to specify it into `.env.json`
file:

```
{
    "DOCS_DIR": "docs/",
    "TUTORIALS_DIR": "path/to/tutorial/directory"
}
```

Run grunt to build the html site for docs:

```
grunt --project=docs
```

and to build the html site for tutorial:

```
grunt --project=tutorials
```

The resulting site is a demo.

## Testing your changes and submitting a PR

When you are ready to submit a PR with your changes you can first test that your changes have been applied correctly against either the PyTorch Docs or Tutorials repo:

1. Run the `grunt build` task on your branch and commit the build to Github.
2. In your local docs or tutorials repo, remove any existing `pytorch_sphinx_theme` packages in the `src` folder (there should be a `pip-delete-this-directory.txt` file there)
3. In `requirements.txt` replace the existing git link with a link pointing to your commit or branch, e.g. `-e git+git://github.com/{ your repo }/pytorch_sphinx_theme.git@{ your commit hash }#egg=pytorch_sphinx_theme`
4. Install the requirements `pip install -r requirements.txt`
5. Remove the current build. In the docs this is `make clean`, tutorials is `make clean-cache`
6. Build the static site. In the docs this is `make html`, tutorials is `make html-noplot`
7. Open the site and look around. In the docs open `docs/build/html/index.html`, in the tutorials open `_build/html.index.html`

If your changes have been applied successfully, remove the build commit from your branch and submit your PR.

## Publishing the theme

Before the new changes are visible in the theme the maintainer will need to run the build process:

```
grunt build
```

Once that is successful commit the change to Github.

### Developing locally against PyTorch Docs and Tutorials

To be able to modify and preview the theme locally against the PyTorch Docs and/or the PyTorch Tutorials first clone the repositories:

- [PyTorch (Docs)](https://github.com/pytorch/pytorch)
- [PyTorch Tutorials](https://github.com/pytorch/tutorials)

Then follow the instructions in each repository to make the docs.

Once the docs have been successfully generated you should be able to run the following to create an html build.

#### Docs

```
# in ./docs
make html
```

#### Tutorials

```
# root directory
make html
```

Once these are successful, navigate to the `conf.py` file in each project. In the Docs these are at `./docs/source`. The Tutorials one can be found in the root directory.

In `conf.py` change the html theme to `pytorch_sphinx_theme` and point the html theme path to this repo's local folder, which will end up looking something like:

```
html_theme = 'pytorch_sphinx_theme'
html_theme_path = ["../../../pytorch_sphinx_theme"]
```

Next create a file `.env.json` in the root of this repo with some keys/values referencing the local folders of the Docs and Tutorials repos:

```
{
  "TUTORIALS_DIR": "../tutorials",
  "DOCS_DIR": "../pytorch/docs/source"
}

```

You can then build the Docs or Tutorials by running

```
grunt --project=docs
```
or

```
grunt --project=tutorials
```

These will generate a live-reloaded local build for the respective projects available at `localhost:1919`.

Note that while live reloading works these two projects are hefty and will take a few seconds to build and reload, especially the Docs.

### Built-in Stylesheets and Fonts

There are a couple of stylesheets and fonts inside the Docs and Tutorials repos themselves meant to override the existing theme. To ensure the most accurate styles we should comment out those files until the maintainers of those repos remove them:

#### Docs

```
# ./docs/source/conf.py

html_context = {
    # 'css_files': [
    #     'https://fonts.googleapis.com/css?family=Lato',
    #     '_static/css/pytorch_theme.css'
    # ],
}
```

#### Tutorials

```
# ./conf.py

# app.add_stylesheet('css/pytorch_theme.css')
# app.add_stylesheet('https://fonts.googleapis.com/css?family=Lato')
```

### Top/Mobile Navigation

The top navigation and mobile menu expect an "active" state for one of the menu items. To ensure that either "Docs" or "Tutorials" is marked as active, set the following config value in the respective `conf.py`, where `{project}` is either `"docs"` or `"tutorials"`.

```
html_theme_options = {
  ...
  'pytorch_project': {project}
  ...
}
```
